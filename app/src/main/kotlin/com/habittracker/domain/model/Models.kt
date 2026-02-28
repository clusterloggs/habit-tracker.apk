package com.habittracker.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

// Domain model for Habit
data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val icon: String = "🎯",
    val color: String = "#2196F3",
    val category: String = "General",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val scheduledTimes: List<LocalTime> = emptyList(),
    val isArchived: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val orderIndex: Int = 0
)

enum class HabitFrequency {
    DAILY,
    SPECIFIC_DAYS,
    WEEKLY
}

// Domain model for Habit Log (Completion)
data class HabitCompletion(
    val id: Long = 0,
    val habitId: Long,
    val completedAt: LocalDateTime,
    val durationMinutes: Int? = null,
    val moodRating: Int? = null, // 1-5
    val notes: String = "",
    val isPartialCompletion: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// Domain model for Device Usage
data class DeviceUsage(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val screenTimeMinutes: Long,
    val date: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// Analytics Models
data class HabitStats(
    val habitId: Long,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val weeklyCompletionRate: Float = 0f, // 0-100%
    val monthlyCompletionRate: Float = 0f,
    val averageDurationMinutes: Int = 0,
    val lastCompletedAt: LocalDateTime? = null,
    val completionsThisWeek: Int = 0,
    val completionsThisMonth: Int = 0
)

data class HeatmapData(
    val date: LocalDateTime,
    val completionCount: Int,
    val intensity: Float // 0-1
)

// Result wrapper for error handling
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
    class Loading<T> : Result<T>()
}

// UI State wrapper
sealed class UiState<T> {
    class Idle<T> : UiState<T>()
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String, val exception: Exception? = null) : UiState<T>()
}
