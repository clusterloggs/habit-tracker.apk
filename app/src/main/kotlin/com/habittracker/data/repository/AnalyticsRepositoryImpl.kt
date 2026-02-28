package com.habittracker.data.repository

import com.habittracker.core.database.dao.HabitLogDao
import com.habittracker.core.database.dao.DeviceUsageLogDao
import com.habittracker.domain.model.HabitStats
import com.habittracker.domain.model.HeatmapData
import com.habittracker.domain.model.InsightCard
import com.habittracker.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class AnalyticsRepositoryImpl(
    private val habitLogDao: HabitLogDao,
    private val deviceUsageLogDao: DeviceUsageLogDao
) : AnalyticsRepository {

    override fun getHabitStats(habitId: Long): Flow<HabitStats> {
        return habitLogDao.getLogsForHabit(habitId).map { logs ->
            val now = LocalDateTime.now()
            val weekAgo = now.minusDays(7)
            val monthAgo = now.minusDays(30)

            val currentStreak = calculateCurrentStreak(logs)
            val longestStreak = calculateLongestStreak(logs)
            val totalCompletions = logs.size
            val weeklyCompletions = logs.count { it.completedAt.isAfter(weekAgo) }
            val monthlyCompletions = logs.count { it.completedAt.isAfter(monthAgo) }

            val weeklyCompletionRate = if (totalCompletions > 0) {
                (weeklyCompletions.toFloat() / 7 * 100).coerceIn(0f, 100f)
            } else 0f

            val monthlyCompletionRate = if (totalCompletions > 0) {
                (monthlyCompletions.toFloat() / 30 * 100).coerceIn(0f, 100f)
            } else 0f

            val averageDuration = if (logs.isNotEmpty()) {
                val validDurations = logs.mapNotNull { it.durationMinutes }
                if (validDurations.isNotEmpty()) {
                    (validDurations.sum() / validDurations.size).roundToInt()
                } else 0
            } else 0

            val lastCompletedAt = logs.maxByOrNull { it.completedAt }?.completedAt

            HabitStats(
                habitId = habitId,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalCompletions = totalCompletions,
                weeklyCompletionRate = weeklyCompletionRate,
                monthlyCompletionRate = monthlyCompletionRate,
                averageDurationMinutes = averageDuration,
                lastCompletedAt = lastCompletedAt,
                completionsThisWeek = weeklyCompletions,
                completionsThisMonth = monthlyCompletions
            )
        }
    }

    override fun getHeatmapData(habitId: Long, months: Int): Flow<List<HeatmapData>> {
        return habitLogDao.getLogsForHabit(habitId).map { logs ->
            val now = LocalDateTime.now()
            val cutoffDate = now.minusMonths(months.toLong())

            val maxCompletionsOnAnyDay = logs.groupBy { it.completedAt.toLocalDate() }
                .maxOfOrNull { it.value.size } ?: 1

            logs.filter { it.completedAt.isAfter(cutoffDate) }
                .groupBy { it.completedAt.toLocalDate() }
                .map { (date, completions) ->
                    HeatmapData(
                        date = date.atStartOfDay(),
                        completionCount = completions.size,
                        intensity = (completions.size.toFloat() / maxCompletionsOnAnyDay)
                            .coerceIn(0f, 1f)
                    )
                }
                .sortedBy { it.date }
        }
    }

    override fun getCorrelationInsights(habitId: Long): Flow<List<InsightCard>> {
        return habitLogDao.getLogsForHabit(habitId).combine(
            deviceUsageLogDao.getTotalScreenTimeForDate(LocalDateTime.now())
        ) { logs, _ ->
            generateInsights(logs)
        }
    }

    private fun generateInsights(completions: List<com.habittracker.core.database.entity.HabitLogEntity>): List<InsightCard> {
        val insights = mutableListOf<InsightCard>()

        if (completions.isNotEmpty()) {
            val lastCompletion = completions.maxByOrNull { it.completedAt }
            val daysSinceLastCompletion = if (lastCompletion != null) {
                ChronoUnit.DAYS.between(
                    lastCompletion.completedAt.toLocalDate(),
                    LocalDateTime.now().toLocalDate()
                ).toInt()
            } else {
                Int.MAX_VALUE
            }

            if (daysSinceLastCompletion == 0) {
                insights.add(
                    InsightCard(
                        title = "Great Job Today!",
                        description = "You completed this habit today. Keep up the momentum!",
                        icon = "🔥",
                        color = "#FF5722",
                        correlationScore = 1f
                    )
                )
            }

            if (daysSinceLastCompletion in 1..2) {
                insights.add(
                    InsightCard(
                        title = "Streak at Risk",
                        description = "Complete today to maintain your streak.",
                        icon = "⚠️",
                        color = "#FFC107",
                        correlationScore = 0.8f
                    )
                )
            }
        }

        return insights
    }

    private fun calculateCurrentStreak(logs: List<com.habittracker.core.database.entity.HabitLogEntity>): Int {
        if (logs.isEmpty()) return 0

        var streak = 0
        val sortedLogs = logs.sortedByDescending { it.completedAt }

        val today = LocalDateTime.now().toLocalDate()
        var expectedDate = today

        for (log in sortedLogs) {
            val logDate = log.completedAt.toLocalDate()
            if (logDate == expectedDate || logDate == expectedDate.minusDays(1)) {
                streak++
                expectedDate = logDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateLongestStreak(logs: List<com.habittracker.core.database.entity.HabitLogEntity>): Int {
        if (logs.isEmpty()) return 0

        var longestStreak = 1
        var currentStreak = 1
        val sortedLogs = logs.sortedBy { it.completedAt }

        for (i in 1 until sortedLogs.size) {
            val previousDate = sortedLogs[i - 1].completedAt.toLocalDate()
            val currentDate = sortedLogs[i].completedAt.toLocalDate()

            if (ChronoUnit.DAYS.between(previousDate, currentDate) == 1L) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return longestStreak
    }
}
