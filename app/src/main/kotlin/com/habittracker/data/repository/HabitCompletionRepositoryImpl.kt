package com.habittracker.data.repository

import com.habittracker.core.database.dao.HabitLogDao
import com.habittracker.core.database.entity.HabitLogEntity
import com.habittracker.domain.model.HabitCompletion
import com.habittracker.domain.model.Result
import com.habittracker.domain.repository.HabitCompletionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class HabitCompletionRepositoryImpl(
    private val habitLogDao: HabitLogDao
) : HabitCompletionRepository {

    override suspend fun logCompletion(completion: HabitCompletion): Result<Long> {
        return try {
            val entity = completion.toEntity()
            val logId = habitLogDao.insert(entity)
            Result.Success(logId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> {
        return habitLogDao.getLogsForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentCompletions(habitId: Long, limit: Int): Flow<List<HabitCompletion>> {
        return habitLogDao.getRecentLogsForHabit(habitId, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCompletionsInDateRange(
        habitId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<HabitCompletion>> {
        return habitLogDao.getLogsInDateRange(habitId, startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateCompletion(completion: HabitCompletion): Result<Unit> {
        return try {
            habitLogDao.update(completion.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteCompletion(completionId: Long): Result<Unit> {
        return try {
            val log = habitLogDao.getLogById(completionId)
            if (log != null) {
                habitLogDao.delete(log)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCompletionCountForDate(habitId: Long, date: LocalDateTime): Flow<Int> {
        return habitLogDao.getCompletionCountForDate(habitId, date)
    }

    override fun getStreakData(habitId: Long): Flow<Pair<Int, Int>> {
        return getRecentCompletions(habitId, limit = 365).map { completions ->
            val currentStreak = calculateCurrentStreak(completions)
            val longestStreak = calculateLongestStreak(completions)
            Pair(currentStreak, longestStreak)
        }
    }

    override fun getLastCompletion(habitId: Long): Flow<HabitCompletion?> {
        return habitLogDao.getLogsForHabit(habitId).map { logs ->
            logs.firstOrNull()?.toDomain()
        }
    }

    private fun calculateCurrentStreak(completions: List<HabitCompletion>): Int {
        if (completions.isEmpty()) return 0

        var streak = 0
        val sortedCompletions = completions.sortedByDescending { it.completedAt }

        val today = LocalDateTime.now().toLocalDate()
        var expectedDate = today

        for (completion in sortedCompletions) {
            val completionDate = completion.completedAt.toLocalDate()
            if (completionDate == expectedDate || completionDate == expectedDate.minusDays(1)) {
                streak++
                expectedDate = completionDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateLongestStreak(completions: List<HabitCompletion>): Int {
        if (completions.isEmpty()) return 0

        var longestStreak = 1
        var currentStreak = 1
        val sortedCompletions = completions.sortedBy { it.completedAt }

        for (i in 1 until sortedCompletions.size) {
            val previousDate = sortedCompletions[i - 1].completedAt.toLocalDate()
            val currentDate = sortedCompletions[i].completedAt.toLocalDate()

            if (ChronoUnit.DAYS.between(previousDate, currentDate) == 1L) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return longestStreak
    }

    private fun HabitCompletion.toEntity(): HabitLogEntity {
        return HabitLogEntity(
            id = id,
            habitId = habitId,
            completedAt = completedAt,
            durationMinutes = durationMinutes,
            moodRating = moodRating,
            notes = notes,
            isPartialCompletion = isPartialCompletion,
            createdAt = createdAt
        )
    }

    private fun HabitLogEntity.toDomain(): HabitCompletion {
        return HabitCompletion(
            id = id,
            habitId = habitId,
            completedAt = completedAt,
            durationMinutes = durationMinutes,
            moodRating = moodRating,
            notes = notes,
            isPartialCompletion = isPartialCompletion,
            createdAt = createdAt
        )
    }
}
