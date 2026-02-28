package com.habittracker.feature.usagestats

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.habittracker.core.database.HabitTrackerDatabase
import com.habittracker.core.database.entity.DeviceUsageLogEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.LocalDateTime
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Collects device usage statistics using UsageStatsManager.
 * Requires PACKAGE_USAGE_STATS permission.
 */
class UsageStatsCollector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: HabitTrackerDatabase,
    private val usageStatsManager: UsageStatsManager,
    private val packageManager: PackageManager
) {

    suspend fun collectDailyUsageStats(): Result<Int> {
        return try {
            if (!hasUsageAccessPermission()) {
                Timber.w("Missing PACKAGE_USAGE_STATS permission")
                return Result.failure(SecurityException("Missing usage stats permission"))
            }

            val calendar = Calendar.getInstance()
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (24 * 60 * 60 * 1000) // Last 24 hours

            val queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ) ?: emptyList()

            val usageLogs = mutableListOf<DeviceUsageLogEntity>()
            val now = LocalDateTime.now()

            for (usageStat in queryUsageStats) {
                if (usageStat.totalTimeInForeground <= 0) continue

                val appName = try {
                    val appInfo = packageManager.getApplicationInfo(usageStat.packageName, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    usageStat.packageName
                }

                val screenTimeMinutes = (usageStat.totalTimeInForeground / 1000 / 60).toLong()

                usageLogs.add(
                    DeviceUsageLogEntity(
                        packageName = usageStat.packageName,
                        appName = appName,
                        screenTimeMinutes = screenTimeMinutes,
                        date = now,
                        createdAt = now
                    )
                )
            }

            if (usageLogs.isNotEmpty()) {
                database.deviceUsageLogDao().insertAll(usageLogs)
                Timber.d("Recorded usage stats for ${usageLogs.size} apps")
            }

            Result.success(usageLogs.size)
        } catch (e: Exception) {
            Timber.e(e, "Failed to collect usage stats")
            Result.failure(e)
        }
    }

    fun hasUsageAccessPermission(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
                appOpsManager?.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                ) == AppOpsManager.MODE_ALLOWED
            } else {
                val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
                appOpsManager?.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
        } catch (e: Exception) {
            false
        }
    }

    fun requestUsageAccessPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * Get app name from package name
     */
    fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    /**
     * Get app icon as drawable resource ID
     */
    fun getAppIcon(packageName: String): Int {
        return try {
            packageManager.getApplicationIcon(packageName).let { -1 }
        } catch (e: Exception) {
            -1
        }
    }
}
