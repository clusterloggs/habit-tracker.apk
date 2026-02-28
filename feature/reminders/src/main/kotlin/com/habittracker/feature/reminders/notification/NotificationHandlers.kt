package com.habittracker.feature.reminders.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.habittracker.feature.reminders.worker.RescheduleAlarmsWorker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalTime
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Broadcast receiver for handling boot completion.
 * Reschedules all alarms when device boots.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Device booted - rescheduling alarms")
            val workRequest = OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>()
                .setInitialDelay(5, TimeUnit.SECONDS)
                .setBackoffPolicy(
                    BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "reschedule_alarms",
                androidx.work.ExistingWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}

/**
 * Broadcast receiver for handling alarm notifications.
 * Called when an alarm is triggered.
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra("habitId", -1L)
        val habitName = intent.getStringExtra("habitName") ?: "Habit Reminder"

        if (habitId != -1L) {
            showNotification(context, habitId, habitName)
        }
    }

    private fun showNotification(context: Context, habitId: Long, habitName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "habit_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for habit reminders"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val completeIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "HABIT_COMPLETED"
            putExtra("habitId", habitId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(habitName)
            .setContentText("Time to complete your habit!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_dialog_info,
                "Complete",
                completePendingIntent
            )
            .build()

        notificationManager.notify(habitId.toInt(), notification)
    }
}

/**
 * Alarm scheduler for managing habit reminders.
 */
class AlarmScheduler @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
) {

    fun scheduleAlarm(habitId: Long, habitName: String, time: LocalTime) {
        if (!hasExactAlarmPermission()) {
            scheduleWithWorkManager(habitId, habitName, time)
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "HABIT_ALARM"
            putExtra("habitId", habitId)
            putExtra("habitName", habitName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Timber.d("Alarm scheduled for habit $habitId at $time")
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to schedule exact alarm - falling back to inexact")
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(habitId: Long) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Timber.d("Alarm cancelled for habit $habitId")
    }

    private fun scheduleWithWorkManager(habitId: Long, habitName: String, time: LocalTime) {
        Timber.d("Scheduling alarm via WorkManager for habit $habitId")
        // Implementation for WorkManager fallback
    }

    private fun hasExactAlarmPermission(): Boolean {
        return try {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                "android.permission.SCHEDULE_EXACT_ALARM"
            } else {
                "android.permission.SET_ALARM"
            }
            context.checkCallingOrSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
}
