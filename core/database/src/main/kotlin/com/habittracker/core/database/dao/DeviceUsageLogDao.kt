package com.habittracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.habittracker.core.database.entity.DeviceUsageLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface DeviceUsageLogDao {

    @Insert
    suspend fun insert(log: DeviceUsageLogEntity): Long

    @Insert
    suspend fun insertAll(logs: List<DeviceUsageLogEntity>)

    @Update
    suspend fun update(log: DeviceUsageLogEntity)

    @Delete
    suspend fun delete(log: DeviceUsageLogEntity)

    @Query("SELECT * FROM device_usage_logs WHERE id = :logId")
    suspend fun getLogById(logId: Long): DeviceUsageLogEntity?

    @Query("SELECT * FROM device_usage_logs WHERE date = :date ORDER BY screen_time_minutes DESC")
    fun getUsageForDate(date: LocalDateTime): Flow<List<DeviceUsageLogEntity>>

    @Query("SELECT * FROM device_usage_logs WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC, screen_time_minutes DESC")
    fun getUsageInDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<DeviceUsageLogEntity>>

    @Query("SELECT * FROM device_usage_logs WHERE package_name = :packageName ORDER BY date DESC LIMIT :limit")
    fun getUsageHistoryForPackage(packageName: String, limit: Int): Flow<List<DeviceUsageLogEntity>>

    @Query("SELECT * FROM device_usage_logs WHERE date = :date ORDER BY screen_time_minutes DESC LIMIT :limit")
    fun getTopAppsForDate(date: LocalDateTime, limit: Int): Flow<List<DeviceUsageLogEntity>>

    @Query("SELECT SUM(screen_time_minutes) FROM device_usage_logs WHERE date = :date")
    fun getTotalScreenTimeForDate(date: LocalDateTime): Flow<Long?>

    @Query("SELECT AVG(screen_time_minutes) FROM device_usage_logs WHERE package_name = :packageName AND date >= :startDate AND date <= :endDate")
    fun getAverageUsageForPackage(packageName: String, startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double?>

    @Query("DELETE FROM device_usage_logs WHERE date < :cutoffDate")
    suspend fun deleteOldLogs(cutoffDate: LocalDateTime)

    @Query("DELETE FROM device_usage_logs WHERE date = :date")
    suspend fun deleteLogsForDate(date: LocalDateTime)
}
