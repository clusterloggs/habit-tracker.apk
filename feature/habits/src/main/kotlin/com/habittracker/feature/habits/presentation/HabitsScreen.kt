package com.habittracker.feature.habits.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habittracker.core.ui.components.EmptyStateMessage
import com.habittracker.core.ui.components.ErrorBanner
import com.habittracker.core.ui.components.HabitCard
import com.habittracker.core.ui.components.LoadingIndicator
import com.habittracker.domain.model.UiState

@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel = hiltViewModel(),
    onHabitClick: (Long) -> Unit = {},
    onCreateHabitClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateHabitClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add Habit")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is UiState.Idle -> {
                    // Do nothing
                }
                is UiState.Loading -> {
                    LoadingIndicator()
                }
                is UiState.Success -> {
                    val state = (uiState as UiState.Success).data
                    if (state.habits.isEmpty()) {
                        EmptyStateMessage(
                            title = "No Habits Yet",
                            message = "Create your first habit to get started!"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.habits) { habit ->
                                HabitCard(
                                    icon = habit.icon,
                                    title = habit.name,
                                    description = habit.description,
                                    color = try {
                                        MaterialTheme.colorScheme.primary
                                    } catch (e: Exception) {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    onClick = { onHabitClick(habit.id) }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    val error = uiState as UiState.Error
                    Column(modifier = Modifier.fillMaxSize()) {
                        ErrorBanner(
                            message = error.message,
                            onDismiss = { viewModel.clearError() }
                        )
                    }
                }
            }
        }
    }
}
