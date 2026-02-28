package com.habittracker.domain.repository

import com.habittracker.domain.model.Habit
import com.habittracker.domain.model.HabitCompletion
import com.habittracker.domain.model.HabitStats
import com.habittracker.domain.model.DeviceUsage
import com.habittracker.domain.model.HeatmapData
import com.habittracker.domain.model.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for Habit operations.
 * Provides abstraction over data sources (Room, Network, etc.)
 */
interface HabitRepository {
    // Create
    suspend fun createHabit(habit: Habit): Result<Long>

    // Read
    fun getHabitById(habitId: Long): Flow<Habit?>
    fun getActiveHabits(): Flow<List<Habit>>
    fun getArchivedHabits(): Flow<List<Habit>>
    fun getAllHabits(): Flow<List<Habit>>
    fun getHabitsByCategory(category: String): Flow<List<Habit>>
    fun getAllCategories(): Flow<List<String>>

    // Update
    suspend fun updateHabit(habit: Habit): Result<Unit>
    suspend fun updateHabitOrder(habitId: Long, newIndex: Int): Result<Unit>

    // Delete
    suspend fun deleteHabit(habitId: Long): Result<Unit>
    suspend fun archiveHabit(habitId: Long): Result<Unit>
    suspend fun unarchiveHabit(habitId: Long): Result<Unit>
}

/**
 * Repository interface for Habit Completion/Log operations
 */
interface HabitCompletionRepository {
    // Create
    suspend fun logCompletion(completion: HabitCompletion): Result<Long>

    // Read
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>
    fun getRecentCompletions(habitId: Long, limit: Int = 30): Flow<List<HabitCompletion>>
    fun getCompletionsInDateRange(
        habitId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<HabitCompletion>>

    // Update
    suspend fun updateCompletion(completion: HabitCompletion): Result<Unit>

    // Delete
    suspend fun deleteCompletion(completionId: Long): Result<Unit>

    // Analytics
    fun getCompletionCountForDate(habitId: Long, date: LocalDateTime): Flow<Int>
    fun getStreakData(habitId: Long): Flow<Pair<Int, Int>> // (currentStreak, longestStreak)
    fun getLastCompletion(habitId: Long): Flow<HabitCompletion?>
}

/**
 * Repository interface for Device Usage tracking
 */
interface DeviceUsageRepository {
    suspend fun recordUsage(usage: DeviceUsage): Result<Long>
    suspend fun recordUsageBatch(usages: List<DeviceUsage>): Result<Unit>

    fun getUsageForDate(date: LocalDateTime): Flow<List<DeviceUsage>>
    fun getUsageInDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<DeviceUsage>>
    fun getTopAppsForDate(date: LocalDateTime, limit: Int = 5): Flow<List<DeviceUsage>>
    fun getUsageHistoryForPackage(packageName: String, limit: Int = 30): Flow<List<DeviceUsage>>

    suspend fun deleteOldUsageLogs(cutoffDate: LocalDateTime): Result<Unit>
}

/**
 * Repository interface for Analytics/Statistics
 */
interface AnalyticsRepository {
    fun getHabitStats(habitId: Long): Flow<HabitStats>
    fun getHeatmapData(habitId: Long, months: Int = 3): Flow<List<HeatmapData>>
    fun getCorrelationInsights(habitId: Long): Flow<List<InsightCard>>
}

data class InsightCard(
    val title: String,
    val description: String,
    val icon: String,
    val color: String,
    val correlationScore: Float // 0-1 indicating strength of correlation
)
