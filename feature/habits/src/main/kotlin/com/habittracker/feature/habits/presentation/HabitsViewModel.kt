package com.habittracker.feature.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habittracker.domain.model.Habit
import com.habittracker.domain.model.HabitFrequency
import com.habittracker.domain.model.Result
import com.habittracker.domain.model.UiState
import com.habittracker.domain.usecase.CreateHabitUseCase
import com.habittracker.domain.usecase.DeleteHabitUseCase
import com.habittracker.domain.usecase.GetActiveHabitsUseCase
import com.habittracker.domain.usecase.UpdateHabitUseCase
import com.habittracker.domain.usecase.ArchiveHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val getActiveHabitsUseCase: GetActiveHabitsUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val archiveHabitUseCase: ArchiveHabitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HabitsScreenState>>(
        UiState.Idle()
    )
    val uiState: StateFlow<UiState<HabitsScreenState>> = _uiState.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        _uiState.update { UiState.Loading() }
        viewModelScope.launch {
            try {
                getActiveHabitsUseCase().collect { habits ->
                    _uiState.update {
                        UiState.Success(HabitsScreenState(habits = habits))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    UiState.Error("Failed to load habits", e)
                }
            }
        }
    }

    fun createHabit(
        name: String,
        description: String,
        icon: String,
        color: String,
        category: String,
        frequency: HabitFrequency,
        scheduledTimes: List<LocalTime>
    ) {
        viewModelScope.launch {
            val habit = Habit(
                name = name,
                description = description,
                icon = icon,
                color = color,
                category = category,
                frequency = frequency,
                scheduledTimes = scheduledTimes,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            when (val result = createHabitUseCase(habit)) {
                is Result.Success -> {
                    loadHabits()
                    _uiState.update { currentState ->
                        if (currentState is UiState.Success) {
                            currentState.copy(
                                data = currentState.data.copy(
                                    lastAction = "Habit created successfully"
                                )
                            )
                        } else currentState
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        UiState.Error("Failed to create habit", result.exception)
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            when (updateHabitUseCase(habit.copy(updatedAt = LocalDateTime.now()))) {
                is Result.Success -> {
                    loadHabits()
                }
                is Result.Error -> {
                    _uiState.update {
                        UiState.Error("Failed to update habit")
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            when (deleteHabitUseCase(habitId)) {
                is Result.Success -> {
                    loadHabits()
                }
                is Result.Error -> {
                    _uiState.update {
                        UiState.Error("Failed to delete habit")
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            when (archiveHabitUseCase(habitId)) {
                is Result.Success -> {
                    loadHabits()
                }
                is Result.Error -> {
                    _uiState.update {
                        UiState.Error("Failed to archive habit")
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { UiState.Idle() }
    }
}

data class HabitsScreenState(
    val habits: List<Habit> = emptyList(),
    val lastAction: String = ""
)
