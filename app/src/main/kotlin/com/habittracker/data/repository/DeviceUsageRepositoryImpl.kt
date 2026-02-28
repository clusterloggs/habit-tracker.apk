package com.habittracker.data.repository

import com.habittracker.core.database.dao.DeviceUsageLogDao
import com.habittracker.core.database.entity.DeviceUsageLogEntity
import com.habittracker.domain.model.DeviceUsage
import com.habittracker.domain.model.Result
import com.habittracker.domain.repository.DeviceUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class DeviceUsageRepositoryImpl(
    private val deviceUsageLogDao: DeviceUsageLogDao
) : DeviceUsageRepository {

    override suspend fun recordUsage(usage: DeviceUsage): Result<Long> {
        return try {
            val entity = usage.toEntity()
            val logId = deviceUsageLogDao.insert(entity)
            Result.Success(logId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun recordUsageBatch(usages: List<DeviceUsage>): Result<Unit> {
        return try {
            val entities = usages.map { it.toEntity() }
            deviceUsageLogDao.insertAll(entities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getUsageForDate(date: LocalDateTime): Flow<List<DeviceUsage>> {
        return deviceUsageLogDao.getUsageForDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUsageInDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<DeviceUsage>> {
        return deviceUsageLogDao.getUsageInDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTopAppsForDate(date: LocalDateTime, limit: Int): Flow<List<DeviceUsage>> {
        return deviceUsageLogDao.getTopAppsForDate(date, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUsageHistoryForPackage(packageName: String, limit: Int): Flow<List<DeviceUsage>> {
        return deviceUsageLogDao.getUsageHistoryForPackage(packageName, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteOldUsageLogs(cutoffDate: LocalDateTime): Result<Unit> {
        return try {
            deviceUsageLogDao.deleteOldLogs(cutoffDate)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun DeviceUsage.toEntity(): DeviceUsageLogEntity {
        return DeviceUsageLogEntity(
            id = id,
            packageName = packageName,
            appName = appName,
            screenTimeMinutes = screenTimeMinutes,
            date = date,
            createdAt = createdAt
        )
    }

    private fun DeviceUsageLogEntity.toDomain(): DeviceUsage {
        return DeviceUsage(
            id = id,
            packageName = packageName,
            appName = appName,
            screenTimeMinutes = screenTimeMinutes,
            date = date,
            createdAt = createdAt
        )
    }
}
