package com.habittracker

import android.app.AlarmManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.work.WorkManager
import com.habittracker.core.database.HabitTrackerDatabase
import com.habittracker.core.datastore.UserPreferencesRepository
import com.habittracker.core.datastore.userPreferencesDataStore
import com.habittracker.data.repository.AnalyticsRepositoryImpl
import com.habittracker.data.repository.DeviceUsageRepositoryImpl
import com.habittracker.data.repository.HabitCompletionRepositoryImpl
import com.habittracker.data.repository.HabitRepositoryImpl
import com.habittracker.domain.repository.AnalyticsRepository
import com.habittracker.domain.repository.DeviceUsageRepository
import com.habittracker.domain.repository.HabitCompletionRepository
import com.habittracker.domain.repository.HabitRepository
import com.habittracker.domain.usecase.ArchiveHabitUseCase
import com.habittracker.domain.usecase.CreateHabitUseCase
import com.habittracker.domain.usecase.DeleteCompletionUseCase
import com.habittracker.domain.usecase.DeleteHabitUseCase
import com.habittracker.domain.usecase.GetActiveHabitsUseCase
import com.habittracker.domain.usecase.GetCompletionCountForDateUseCase
import com.habittracker.domain.usecase.GetHabitByIdUseCase
import com.habittracker.domain.usecase.GetHabitCompletionsUseCase
import com.habittracker.domain.usecase.GetLastCompletionUseCase
import com.habittracker.domain.usecase.GetStreakUseCase
import com.habittracker.domain.usecase.LogHabitCompletionUseCase
import com.habittracker.domain.usecase.UnarchiveHabitUseCase
import com.habittracker.domain.usecase.UpdateHabitUseCase
import com.habittracker.feature.reminders.notification.AlarmScheduler
import com.habittracker.feature.usagestats.UsageStatsCollector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module for providing singleton dependencies across the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHabitTrackerDatabase(@ApplicationContext context: Context): HabitTrackerDatabase {
        return HabitTrackerDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context.userPreferencesDataStore)
    }

    // Repository Providers
    @Provides
    @Singleton
    fun provideHabitRepository(database: HabitTrackerDatabase): HabitRepository {
        return HabitRepositoryImpl(
            database.habitDao(),
            database.habitScheduleDao(),
            database.alarmDao()
        )
    }

    @Provides
    @Singleton
    fun provideHabitCompletionRepository(database: HabitTrackerDatabase): HabitCompletionRepository {
        return HabitCompletionRepositoryImpl(database.habitLogDao())
    }

    @Provides
    @Singleton
    fun provideDeviceUsageRepository(database: HabitTrackerDatabase): DeviceUsageRepository {
        return DeviceUsageRepositoryImpl(database.deviceUsageLogDao())
    }

    @Provides
    @Singleton
    fun provideAnalyticsRepository(database: HabitTrackerDatabase): AnalyticsRepository {
        return AnalyticsRepositoryImpl(
            database.habitLogDao(),
            database.deviceUsageLogDao()
        )
    }

    // Use Case Providers
    @Provides
    @Singleton
    fun provideCreateHabitUseCase(repository: HabitRepository): CreateHabitUseCase {
        return CreateHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateHabitUseCase(repository: HabitRepository): UpdateHabitUseCase {
        return UpdateHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteHabitUseCase(repository: HabitRepository): DeleteHabitUseCase {
        return DeleteHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetActiveHabitsUseCase(repository: HabitRepository): GetActiveHabitsUseCase {
        return GetActiveHabitsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetHabitByIdUseCase(repository: HabitRepository): GetHabitByIdUseCase {
        return GetHabitByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideArchiveHabitUseCase(repository: HabitRepository): ArchiveHabitUseCase {
        return ArchiveHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUnarchiveHabitUseCase(repository: HabitRepository): UnarchiveHabitUseCase {
        return UnarchiveHabitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLogHabitCompletionUseCase(repository: HabitCompletionRepository): LogHabitCompletionUseCase {
        return LogHabitCompletionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetHabitCompletionsUseCase(repository: HabitCompletionRepository): GetHabitCompletionsUseCase {
        return GetHabitCompletionsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStreakUseCase(repository: HabitCompletionRepository): GetStreakUseCase {
        return GetStreakUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLastCompletionUseCase(repository: HabitCompletionRepository): GetLastCompletionUseCase {
        return GetLastCompletionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCompletionCountForDateUseCase(repository: HabitCompletionRepository): GetCompletionCountForDateUseCase {
        return GetCompletionCountForDateUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteCompletionUseCase(repository: HabitCompletionRepository): DeleteCompletionUseCase {
        return DeleteCompletionUseCase(repository)
    }

    // System Service Providers
    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideUsageStatsManager(@ApplicationContext context: Context): UsageStatsManager {
        return context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    // Feature Providers
    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context,
        alarmManager: AlarmManager
    ): AlarmScheduler {
        return AlarmScheduler(context, alarmManager)
    }

    @Provides
    @Singleton
    fun provideUsageStatsCollector(
        @ApplicationContext context: Context,
        database: HabitTrackerDatabase,
        usageStatsManager: UsageStatsManager,
        packageManager: PackageManager
    ): UsageStatsCollector {
        return UsageStatsCollector(context, database, usageStatsManager, packageManager)
    }
}

/**
 * Application class for HabitTracker
 */
@dagger.hilt.android.HiltAndroidApp
class HabitTrackerApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging (debug only in production)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
