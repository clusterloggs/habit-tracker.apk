package com.habittracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import androidx.room.Upsert
import com.habittracker.core.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Upsert
    suspend fun upsert(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitByIdFlow(habitId: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY order_index ASC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 1")
    fun getArchivedHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY order_index ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 AND category = :category ORDER BY order_index ASC")
    fun getHabitsByCategory(category: String): Flow<List<HabitEntity>>

    @Query("UPDATE habits SET order_index = :newIndex WHERE id = :habitId")
    suspend fun updateHabitOrder(habitId: Long, newIndex: Int)

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :habitId")
    suspend fun archiveHabit(habitId: Long)

    @Query("UPDATE habits SET isArchived = 0 WHERE id = :habitId")
    suspend fun unarchiveHabit(habitId: Long)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Query("SELECT COUNT(*) FROM habits WHERE isArchived = 0")
    fun getActiveHabitsCount(): Flow<Int>

    @Query("SELECT DISTINCT category FROM habits WHERE isArchived = 0 ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
}
