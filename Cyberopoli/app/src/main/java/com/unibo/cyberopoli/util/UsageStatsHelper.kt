package com.unibo.cyberopoli.util

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.app.NotificationManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

data class SessionStats(
    val sessionCount: Int,
    val averageSessionDuration: Long,
    val unlockCount: Int
)

class UsageStatsHelper(private val context: Context) {

    suspend fun getTopUsedApps(limit: Int = 10): List<Pair<String, Long>> =
        withContext(Dispatchers.IO) {
            val stats = (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
                .queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                    System.currentTimeMillis()
                )
            stats.sortedByDescending { it.totalTimeInForeground }
                .take(limit)
                .map { it.packageName to it.totalTimeInForeground }
        }

    suspend fun getTodayUsageTime(): Long =
        withContext(Dispatchers.IO) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val stats = (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
                .queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    cal.timeInMillis,
                    System.currentTimeMillis()
                )
            stats.sumOf { it.totalTimeInForeground }
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
                unlockCount = unlocks
            )
        }

    fun isDoNotDisturbEnabled(): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE
    }

    fun getInterruptionFilter(): Int {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.currentInterruptionFilter
    }
}
