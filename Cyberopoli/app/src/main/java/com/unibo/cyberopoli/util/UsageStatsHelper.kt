package com.unibo.cyberopoli.util

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.util.Calendar

class UsageStatsHelper(private val context: Context) {

    fun logUsageStats() {
        val usageStatsList = getMostUsedApps()
        usageStatsList.take(5).forEach {
            Log.d(
                "APP_USAGE",
                "App: ${it.packageName} - Tempo in primo piano: ${it.totalTimeInForeground / 1000} sec"
            )
        }

        val usageTimeMillis = getTodayUsageTime()
        Log.d("USAGE_TIME", "Tempo totale di utilizzo oggi: ${usageTimeMillis / 1000} secondi")
    }

    private fun getMostUsedApps(): List<UsageStats> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 24 * 60 * 60 * 1000
        val stats =
            usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return stats.sortedByDescending { it.totalTimeInForeground }
    }

    private fun getTodayUsageTime(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats =
            usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        val totalUsageTimeMillis = stats.sumOf { it.totalTimeInForeground }
        return totalUsageTimeMillis
    }
}