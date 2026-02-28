package com.habittracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.habittracker.core.database.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface HabitLogDao {

    @Insert
    suspend fun insert(log: HabitLogEntity): Long

    @Update
    suspend fun update(log: HabitLogEntity)

    @Delete
    suspend fun delete(log: HabitLogEntity)

    @Query("SELECT * FROM habit_logs WHERE id = :logId")
    suspend fun getLogById(logId: Long): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE habit_id = :habitId ORDER BY completed_at DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habit_id = :habitId ORDER BY completed_at DESC LIMIT :limit")
    fun getRecentLogsForHabit(habitId: Long, limit: Int): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habit_id = :habitId AND completed_at >= :startDate AND completed_at <= :endDate ORDER BY completed_at DESC")
    fun getLogsInDateRange(habitId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<HabitLogEntity>>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habit_id = :habitId AND completed_at >= :date")
    fun getCompletionCountSince(habitId: Long, date: LocalDateTime): Flow<Int>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habit_id = :habitId AND DATE(completed_at) = DATE(:date)")
    suspend fun getCompletionCountForDate(habitId: Long, date: LocalDateTime): Int

    @Query("DELETE FROM habit_logs WHERE habit_id = :habitId")
    suspend fun deleteLogsForHabit(habitId: Long)

    @Query("SELECT AVG(duration_minutes) FROM habit_logs WHERE habit_id = :habitId AND duration_minutes IS NOT NULL")
    fun getAverageDurationForHabit(habitId: Long): Flow<Double?>

    @Query("SELECT * FROM habit_logs WHERE habit_id = :habitId AND mood_rating IS NOT NULL ORDER BY completed_at DESC LIMIT :limit")
    fun getMoodLogsForHabit(habitId: Long, limit: Int): Flow<List<HabitLogEntity>>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habit_id = :habitId")
    fun getTotalCompletionCount(habitId: Long): Flow<Int>

    @Query("""
        SELECT * FROM habit_logs
        WHERE habit_id = :habitId
        AND completed_at = (SELECT MAX(completed_at) FROM habit_logs WHERE habit_id = :habitId)
    """)
    suspend fun getLastCompletion(habitId: Long): HabitLogEntity?
}
