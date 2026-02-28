package com.habittracker.feature.analytics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habittracker.domain.model.HabitStats
import com.habittracker.domain.model.UiState
import com.habittracker.domain.repository.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HabitStats>>(UiState.Idle())
    val uiState: StateFlow<UiState<HabitStats>> = _uiState.asStateFlow()

    fun loadStats(habitId: Long) {
        _uiState.update { UiState.Loading() }
        viewModelScope.launch {
            try {
                analyticsRepository.getHabitStats(habitId).collect { stats ->
                    _uiState.update { UiState.Success(stats) }
                }
            } catch (e: Exception) {
                _uiState.update { UiState.Error("Failed to load analytics", e) }
            }
        }
    }
}
