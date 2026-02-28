package com.habittracker.feature.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habittracker.core.ui.components.LoadingIndicator
import com.habittracker.domain.model.UiState

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    habitId: Long = -1L
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (uiState) {
            is UiState.Loading -> {
                LoadingIndicator()
            }
            is UiState.Success -> {
                val stats = (uiState as UiState.Success).data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        StatsCard(
                            title = "Current Streak",
                            value = "${stats.currentStreak} days",
                            icon = "🔥"
                        )
                    }
                    item {
                        StatsCard(
                            title = "Longest Streak",
                            value = "${stats.longestStreak} days",
                            icon = "🏆"
                        )
                    }
                    item {
                        StatsCard(
                            title = "Total Completions",
                            value = stats.totalCompletions.toString(),
                            icon = "✅"
                        )
                    }
                    item {
                        StatsCard(
                            title = "Weekly Rate",
                            value = "%.1f%%".format(stats.weeklyCompletionRate),
                            icon = "📊"
                        )
                    }
                    item {
                        StatsCard(
                            title = "Monthly Rate",
                            value = "%.1f%%".format(stats.monthlyCompletionRate),
                            icon = "📈"
                        )
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    text = "Failed to load analytics: ${(uiState as UiState.Error).message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is UiState.Idle -> {}
        }
    }
}

@Composable
private fun StatsCard(
    title: String,
    value: String,
    icon: String
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$icon $title",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
