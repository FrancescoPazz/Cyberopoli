package com.unibo.cyberopoli.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import android.os.Process
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS

data class SessionStats(
    val sessionCount: Int,
    val averageSessionDuration: Long,
    val unlockCount: Int,
)

fun Context.hasUsageStatsPermission(): Boolean {
    val am = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = am.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun Context.openUsageAccessSettings() {
    startActivity(
        Intent(ACTION_USAGE_ACCESS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

class UsageStatsHelper(private val context: Context) {
    suspend fun getTopUsedApps(limit: Int = 15): List<Pair<String, Double>> =
        withContext(Dispatchers.IO) {
            Log.d("TESTONE", "getTopUsedApps called")
            val stats =
                (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager).queryUsageStats(
                    UsageStatsManager.INTERVAL_WEEKLY,
                    System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                    System.currentTimeMillis(),
                )
            Log.d("TESTONE", "Usage stats size: ${stats.size}: ${stats.joinToString { it.packageName }}")
            stats.sortedByDescending { it.totalTimeInForeground }.take(limit)
                .map { it.packageName to (it.totalTimeInForeground / 3_600_000.0) }
        }

    suspend fun getWeeklyUsageTime(): Double =
        withContext(Dispatchers.IO) {
            val cal =
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -7)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            val stats =
                (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager).queryUsageStats(
                    UsageStatsManager.INTERVAL_WEEKLY,
                    cal.timeInMillis,
                    System.currentTimeMillis(),
                )
            val totalMillis = stats.sumOf { it.totalTimeInForeground }
            totalMillis / 3_600_000.0
        }

    suspend fun getSessionStats(pastMillis: Long = 24 * 60 * 60 * 1000): SessionStats =
        withContext(Dispatchers.IO) {
            val usageManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val now = System.currentTimeMillis()
            val usageEvents = usageManager.queryEvents(now - pastMillis, now)

            var unlocks = 0
            var lastOn: Long? = null
            val sessionDurations = mutableListOf<Long>()

            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                when (event.eventType) {
                    UsageEvents.Event.SCREEN_INTERACTIVE -> {
                        lastOn = event.timeStamp
                        unlocks++
                    }

                    UsageEvents.Event.SCREEN_NON_INTERACTIVE -> {
                        lastOn?.let { start ->
                            val dur = event.timeStamp - start
                            if (dur > 0) sessionDurations += dur
                            lastOn = null
                        }
                    }
                }
            }

            val count = sessionDurations.size
            val avg = if (count > 0) sessionDurations.sum() / count else 0L
            SessionStats(
                sessionCount = count,
                averageSessionDuration = avg,
                unlockCount = unlocks,
            )
        }
}
