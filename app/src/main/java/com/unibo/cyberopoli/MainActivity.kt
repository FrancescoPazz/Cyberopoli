package com.unibo.cyberopoli

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.unibo.cyberopoli.ui.CyberopoliNavGraph
import com.unibo.cyberopoli.ui.screens.auth.AuthViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()
        setContent {
            val navController = rememberNavController()
            CyberopoliNavGraph(navController = navController, authViewModel = authViewModel)
        }
    }
}