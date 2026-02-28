package com.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habittracker.core.ui.theme.HabitTrackerTheme
import com.habittracker.feature.analytics.presentation.AnalyticsScreen
import com.habittracker.feature.habits.presentation.HabitsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                HabitTrackerApp()
            }
        }
    }
}

@Composable
fun HabitTrackerApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = HabitsRoute
        ) {
            composable<HabitsRoute> {
                HabitsScreen(
                    onCreateHabitClick = { navController.navigate(CreateHabitRoute) },
                    onHabitClick = { habitId ->
                        navController.navigate(HabitDetailRoute(habitId))
                    }
                )
            }

            composable<HabitDetailRoute> { backStackEntry ->
                val route = backStackEntry.destination.route as? String
                val habitId = route?.substringAfterLast("/")?.toLongOrNull() ?: -1L
                AnalyticsScreen(habitId = habitId)
            }

            composable<CreateHabitRoute> {
                CreateHabitScreen(onHabitCreated = { navController.popBackStack() })
            }
        }
    }
}

@Serializable
object HabitsRoute

@Serializable
data class HabitDetailRoute(val habitId: Long)

@Serializable
object CreateHabitRoute

@Composable
fun CreateHabitScreen(
    onHabitCreated: () -> Unit = {}
) {
    androidx.compose.material3.Text("Create Habit Screen - TODO")
}
