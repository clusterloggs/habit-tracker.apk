package com.habittracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(
    tableName = "habits",
    indices = [
        Index(value = ["name"]),
        Index(value = ["isArchived"])
    ]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val icon: String = "🎯", // Emoji icon
    val color: String = "#2196F3", // Material Color
    val category: String = "General",
    val frequency: String, // DAILY, SPECIFIC_DAYS, WEEKLY
    val targetDays: String = "", // Comma-separated days for SPECIFIC_DAYS (0=Sun, 1=Mon, etc.)
    val isArchived: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0 // For custom ordering
)

@Entity(
    tableName = "habit_schedules",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habit_id"])
    ]
)
data class HabitScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "habit_id")
    val habitId: Long,
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: LocalTime, // Time of day
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
)

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habit_id"]),
        Index(value = ["completed_at"]),
        Index(value = ["habit_id", "completed_at"])
    ]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "habit_id")
    val habitId: Long,
    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime,
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int? = null, // Optional: manually tracked or timer-based
    @ColumnInfo(name = "mood_rating")
    val moodRating: Int? = null, // 1-5 scale
    val notes: String = "",
    val isPartialCompletion: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
)

@Entity(
    tableName = "device_usage_logs",
    indices = [
        Index(value = ["date"]),
        Index(value = ["package_name"])
    ]
)
data class DeviceUsageLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "app_name")
    val appName: String = "",
    @ColumnInfo(name = "screen_time_minutes")
    val screenTimeMinutes: Long,
    val date: LocalDateTime,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
)

@Entity(
    tableName = "alarms",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habit_id"]),
        Index(value = ["alarm_id"])
    ]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "habit_id")
    val habitId: Long,
    @ColumnInfo(name = "alarm_id")
    val alarmId: Int, // RequestCode for AlarmManager
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: LocalTime,
    val day: Int = -1, // -1 for daily, 0-6 for specific days
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime
)
