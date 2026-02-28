package com.habittracker.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val EXACT_ALARMS_ENABLED = booleanPreferencesKey("exact_alarms_enabled")
        private val GRACE_PERIOD_HOURS = intPreferencesKey("grace_period_hours")
        private val THEME_MODE = stringPreferencesKey("theme_mode") // LIGHT, DARK, SYSTEM
        private val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        private val NOTIFICATION_SOUND_ENABLED = booleanPreferencesKey("notification_sound_enabled")
        private val NOTIFICATION_VIBRATION_ENABLED = booleanPreferencesKey("notification_vibration_enabled")
        private val USAGE_STATS_COLLECTION_ENABLED = booleanPreferencesKey("usage_stats_collection_enabled")
        private val LAST_USAGE_STATS_UPDATE = stringPreferencesKey("last_usage_stats_update")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val exactAlarmsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[EXACT_ALARMS_ENABLED] ?: true
    }

    val gracePeriodHours: Flow<Int> = dataStore.data.map { preferences ->
        preferences[GRACE_PERIOD_HOURS] ?: 24
    }

    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "SYSTEM"
    }

    val dynamicColorEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_ENABLED] ?: true
    }

    val notificationSoundEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_SOUND_ENABLED] ?: true
    }

    val notificationVibrationEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_VIBRATION_ENABLED] ?: true
    }

    val usageStatsCollectionEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[USAGE_STATS_COLLECTION_ENABLED] ?: true
    }

    val lastUsageStatsUpdate: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LAST_USAGE_STATS_UPDATE]
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setExactAlarmsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[EXACT_ALARMS_ENABLED] = enabled
        }
    }

    suspend fun setGracePeriodHours(hours: Int) {
        dataStore.edit { preferences ->
            preferences[GRACE_PERIOD_HOURS] = hours
        }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_ENABLED] = enabled
        }
    }

    suspend fun setNotificationSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_SOUND_ENABLED] = enabled
        }
    }

    suspend fun setNotificationVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setUsageStatsCollectionEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[USAGE_STATS_COLLECTION_ENABLED] = enabled
        }
    }

    suspend fun setLastUsageStatsUpdate(timestamp: String) {
        dataStore.edit { preferences ->
            preferences[LAST_USAGE_STATS_UPDATE] = timestamp
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
