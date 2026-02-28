package com.habittracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.habittracker.core.database.entity.HabitScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

@Dao
interface HabitScheduleDao {

    @Insert
    suspend fun insert(schedule: HabitScheduleEntity): Long

    @Insert
    suspend fun insertAll(schedules: List<HabitScheduleEntity>)

    @Update
    suspend fun update(schedule: HabitScheduleEntity)

    @Delete
    suspend fun delete(schedule: HabitScheduleEntity)

    @Query("SELECT * FROM habit_schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Long): HabitScheduleEntity?

    @Query("SELECT * FROM habit_schedules WHERE habit_id = :habitId ORDER BY scheduled_time ASC")
    fun getSchedulesForHabit(habitId: Long): Flow<List<HabitScheduleEntity>>

    @Query("SELECT * FROM habit_schedules WHERE habit_id = :habitId")
    suspend fun getSchedulesForHabitOnce(habitId: Long): List<HabitScheduleEntity>

    @Query("DELETE FROM habit_schedules WHERE habit_id = :habitId")
    suspend fun deleteSchedulesForHabit(habitId: Long)

    @Query("DELETE FROM habit_schedules WHERE id = :scheduleId")
    suspend fun deleteSchedule(scheduleId: Long)

    @Query("SELECT COUNT(*) FROM habit_schedules WHERE habit_id = :habitId")
    fun getScheduleCountForHabit(habitId: Long): Flow<Int>
}
