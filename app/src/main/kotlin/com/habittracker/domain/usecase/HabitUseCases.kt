package com.habittracker.domain.usecase

import com.habittracker.domain.model.Habit
import com.habittracker.domain.model.HabitCompletion
import com.habittracker.domain.model.Result
import com.habittracker.domain.repository.HabitRepository
import com.habittracker.domain.repository.HabitCompletionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

// Habit Use Cases
class CreateHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habit: Habit): Result<Long> = habitRepository.createHabit(habit)
}

class UpdateHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habit: Habit): Result<Unit> = habitRepository.updateHabit(habit)
}

class DeleteHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habitId: Long): Result<Unit> = habitRepository.deleteHabit(habitId)
}

class GetActiveHabitsUseCase(private val habitRepository: HabitRepository) {
    operator fun invoke(): Flow<List<Habit>> = habitRepository.getActiveHabits()
}

class GetHabitByIdUseCase(private val habitRepository: HabitRepository) {
    operator fun invoke(habitId: Long): Flow<Habit?> = habitRepository.getHabitById(habitId)
}

class GetHabitsByCategoryUseCase(private val habitRepository: HabitRepository) {
    operator fun invoke(category: String): Flow<List<Habit>> = habitRepository.getHabitsByCategory(category)
}

class ArchiveHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habitId: Long): Result<Unit> = habitRepository.archiveHabit(habitId)
}

class UnarchiveHabitUseCase(private val habitRepository: HabitRepository) {
    suspend operator fun invoke(habitId: Long): Result<Unit> = habitRepository.unarchiveHabit(habitId)
}

// Habit Completion Use Cases
class LogHabitCompletionUseCase(private val completionRepository: HabitCompletionRepository) {
    suspend operator fun invoke(completion: HabitCompletion): Result<Long> {
        return completionRepository.logCompletion(completion)
    }
}

class GetHabitCompletionsUseCase(private val completionRepository: HabitCompletionRepository) {
    operator fun invoke(habitId: Long): Flow<List<HabitCompletion>> {
        return completionRepository.getCompletionsForHabit(habitId)
    }
}

class GetStreakUseCase(private val completionRepository: HabitCompletionRepository) {
    operator fun invoke(habitId: Long): Flow<Pair<Int, Int>> {
        return completionRepository.getStreakData(habitId)
    }
}

class GetLastCompletionUseCase(private val completionRepository: HabitCompletionRepository) {
    operator fun invoke(habitId: Long): Flow<HabitCompletion?> {
        return completionRepository.getLastCompletion(habitId)
    }
}

class GetCompletionCountForDateUseCase(private val completionRepository: HabitCompletionRepository) {
    operator fun invoke(habitId: Long, date: LocalDateTime): Flow<Int> {
        return completionRepository.getCompletionCountForDate(habitId, date)
    }
}

class DeleteCompletionUseCase(private val completionRepository: HabitCompletionRepository) {
    suspend operator fun invoke(completionId: Long): Result<Unit> {
        return completionRepository.deleteCompletion(completionId)
    }
}
