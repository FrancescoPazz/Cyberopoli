package com.unibo.cyberopoli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.unibo.cyberopoli.ui.CyberopoliNavGraph
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel
import com.unibo.cyberopoli.util.PermissionHandler
import com.unibo.cyberopoli.util.UsageStatsHelper

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var usageStatsHelper: UsageStatsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHandler = PermissionHandler(this)
        usageStatsHelper = UsageStatsHelper(this)

        permissionHandler.requestCameraPermission()

        if (permissionHandler.hasUsageStatsPermission()) {
            usageStatsHelper.logUsageStats()
        } else {
            permissionHandler.requestUsageStatsPermission()
        }

        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()
        setContent {
            val navController = rememberNavController()
            CyberopoliNavGraph(navController = navController, authViewModel = authViewModel)
        }
    }
}