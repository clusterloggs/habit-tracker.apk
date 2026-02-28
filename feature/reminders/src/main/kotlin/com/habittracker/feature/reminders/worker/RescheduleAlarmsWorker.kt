package com.habittracker.feature.reminders.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.habittracker.core.database.HabitTrackerDatabase
import com.habittracker.feature.reminders.notification.AlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Worker for rescheduling alarms after device boot or app update.
 */
@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val database: HabitTrackerDatabase,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val alarmDao = database.alarmDao()
            val habitDao = database.habitDao()

            // Get all enabled alarms
            val alarms = alarmDao.getEnabledAlarms().collect { alarmList ->
                alarmList.forEach { alarm ->
                    val habit = habitDao.getHabitById(alarm.habitId)
                    if (habit != null) {
                        alarmScheduler.scheduleAlarm(
                            habitId = alarm.habitId,
                            habitName = habit.name,
                            time = alarm.scheduledTime
                        )
                    }
                }
            }

            Timber.d("Successfully rescheduled all alarms")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to reschedule alarms")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
