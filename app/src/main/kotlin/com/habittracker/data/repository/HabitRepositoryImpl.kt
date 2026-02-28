package com.habittracker.data.repository

import com.habittracker.core.database.dao.AlarmDao
import com.habittracker.core.database.dao.HabitDao
import com.habittracker.core.database.dao.HabitScheduleDao
import com.habittracker.core.database.entity.AlarmEntity
import com.habittracker.core.database.entity.HabitEntity
import com.habittracker.core.database.entity.HabitScheduleEntity
import com.habittracker.domain.model.Habit
import com.habittracker.domain.model.HabitFrequency
import com.habittracker.domain.model.Result
import com.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val habitScheduleDao: HabitScheduleDao,
    private val alarmDao: AlarmDao
) : HabitRepository {

    override suspend fun createHabit(habit: Habit): Result<Long> {
        return try {
            val entity = habit.toEntity()
            val habitId = habitDao.insert(entity)

            // Create schedules for the habit
            val schedules = habit.scheduledTimes.map { time ->
                HabitScheduleEntity(
                    habitId = habitId,
                    scheduledTime = time,
                    createdAt = LocalDateTime.now()
                )
            }
            if (schedules.isNotEmpty()) {
                habitScheduleDao.insertAll(schedules)
            }

            // Create alarms for the habit
            val alarms = habit.scheduledTimes.mapIndexed { index, time ->
                AlarmEntity(
                    habitId = habitId,
                    alarmId = (habitId * 1000 + index).toInt(),
                    scheduledTime = time,
                    isEnabled = true,
                    createdAt = LocalDateTime.now()
                )
            }
            if (alarms.isNotEmpty()) {
                alarmDao.insertAll(alarms)
            }

            Result.Success(habitId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getHabitById(habitId: Long): Flow<Habit?> {
        return habitDao.getHabitByIdFlow(habitId).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getActiveHabits(): Flow<List<Habit>> {
        return habitDao.getActiveHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getArchivedHabits(): Flow<List<Habit>> {
        return habitDao.getArchivedHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHabitsByCategory(category: String): Flow<List<Habit>> {
        return habitDao.getHabitsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllCategories(): Flow<List<String>> {
        return habitDao.getAllCategories()
    }

    override suspend fun updateHabit(habit: Habit): Result<Unit> {
        return try {
            habitDao.update(habit.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateHabitOrder(habitId: Long, newIndex: Int): Result<Unit> {
        return try {
            habitDao.updateHabitOrder(habitId, newIndex)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteHabit(habitId: Long): Result<Unit> {
        return try {
            habitDao.deleteHabit(habitId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun archiveHabit(habitId: Long): Result<Unit> {
        return try {
            habitDao.archiveHabit(habitId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unarchiveHabit(habitId: Long): Result<Unit> {
        return try {
            habitDao.unarchiveHabit(habitId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun Habit.toEntity(): HabitEntity {
        return HabitEntity(
            id = id,
            name = name,
            description = description,
            icon = icon,
            color = color,
            category = category,
            frequency = frequency.name,
            targetDays = "",
            isArchived = isArchived,
            createdAt = createdAt,
            updatedAt = updatedAt,
            orderIndex = orderIndex
        )
    }

    private fun HabitEntity.toDomain(): Habit {
        return Habit(
            id = id,
            name = name,
            description = description,
            icon = icon,
            color = color,
            category = category,
            frequency = HabitFrequency.valueOf(frequency),
            scheduledTimes = emptyList(), // Loaded separately if needed
            isArchived = isArchived,
            createdAt = createdAt,
            updatedAt = updatedAt,
            orderIndex = orderIndex
        )
    }
}
