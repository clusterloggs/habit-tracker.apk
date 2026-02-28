package com.habittracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.habittracker.core.database.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert
    suspend fun insert(alarm: AlarmEntity): Long

    @Insert
    suspend fun insertAll(alarms: List<AlarmEntity>)

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Long): AlarmEntity?

    @Query("SELECT * FROM alarms WHERE habit_id = :habitId ORDER BY scheduled_time ASC")
    fun getAlarmsForHabit(habitId: Long): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE habit_id = :habitId")
    suspend fun getAlarmsForHabitOnce(habitId: Long): List<AlarmEntity>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY scheduled_time ASC")
    fun getEnabledAlarms(): Flow<List<AlarmEntity>>

    @Query("UPDATE alarms SET isEnabled = :isEnabled WHERE id = :alarmId")
    suspend fun updateAlarmEnabled(alarmId: Long, isEnabled: Boolean)

    @Query("UPDATE alarms SET scheduled_time = :newTime WHERE id = :alarmId")
    suspend fun updateAlarmTime(alarmId: Long, newTime: String)

    @Query("DELETE FROM alarms WHERE habit_id = :habitId")
    suspend fun deleteAlarmsForHabit(habitId: Long)

    @Query("DELETE FROM alarms WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Long)

    @Query("SELECT COUNT(*) FROM alarms WHERE habit_id = :habitId AND isEnabled = 1")
    fun getEnabledAlarmCountForHabit(habitId: Long): Flow<Int>

    @Query("SELECT DISTINCT day FROM alarms WHERE habit_id = :habitId ORDER BY day ASC")
    suspend fun getScheduledDaysForHabit(habitId: Long): List<Int>
}
