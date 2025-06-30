package com.unibo.cyberopoli.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

fun Context.openUsageAccessSettings() {
    startActivity(
        Intent(ACTION_USAGE_ACCESS_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

class UsageStatsHelper(private val context: Context) {
    suspend fun getTopUsedApps(limit: Int = 15): List<Pair<String, Double>> =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val oneWeekAgo = now - 7L * 24 * 60 * 60 * 1000
            val usageStats = (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
                .queryUsageStats(
                    UsageStatsManager.INTERVAL_WEEKLY,
                    oneWeekAgo,
                    now
                )
            val summedByPackage: List<Pair<String, Long>> = usageStats
                .groupBy { it.packageName }
                .map { (pkg, statsList) ->
                    val total = statsList.sumOf { it.totalTimeInForeground }
                    pkg to total
                }
            val topList = summedByPackage
                .sortedByDescending { it.second }
                .take(limit)
            val result = topList.map { (pkg, totalMillis) ->
                val appName = try {
                    val ai = context.packageManager.getApplicationInfo(pkg, 0)
                    context.packageManager.getApplicationLabel(ai).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    pkg
                }
                val hours = totalMillis / 3_600_000.0
                appName to hours
            }
            result
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
