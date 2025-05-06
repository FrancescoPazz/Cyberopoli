package com.unibo.cyberopoli.util

import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class UsageStatsHelper(private val context: Context) {

    suspend fun getTopUsedApps(limit: Int = 5): List<Pair<String, Long>> =
        withContext(Dispatchers.IO) {
            val stats = (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
                .queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    System.currentTimeMillis() - 24*60*60*1000,
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
}
