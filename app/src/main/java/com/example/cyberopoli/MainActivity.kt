package com.example.cyberopoli

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.cyberopoli.ui.CyberopoliNavGraph
import com.example.cyberopoli.ui.theme.CyberopoliTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasUsageStatsPermission(this)) {
            requestUsageStatsPermission()
        } else {
            val usageStatsList = getMostUsedApps(this)
            usageStatsList.take(5).forEach {
                Log.d("APP_USAGE", "App: ${it.packageName} - Tempo in primo piano: ${it.totalTimeInForeground / 1000} sec")

                val usageTimeMillis = getTodayUsageTime(this)
                Log.d("USAGE_TIME", "Tempo totale di utilizzo oggi: ${usageTimeMillis / 1000} secondi")
            }
        }

        enableEdgeToEdge()
        setContent {
            CyberopoliTheme {
                val navController = rememberNavController()
                CyberopoliNavGraph(navController)
            }
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        } else {
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun getMostUsedApps(context: Context): List<UsageStats> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 24 * 60 * 60 * 1000
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return stats.sortedByDescending { it.totalTimeInForeground }
    }

    private fun getTodayUsageTime(context: Context): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        val totalUsageTimeMillis = stats.sumOf { it.totalTimeInForeground }
        return totalUsageTimeMillis
    }
}