package com.example.cyberopoli.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cyberopoli.ui.screens.auth.AuthScreen
import com.example.cyberopoli.ui.screens.auth.GuestScreen
import com.example.cyberopoli.ui.screens.auth.LoginScreen
import com.example.cyberopoli.ui.screens.auth.SignUpScreen
import com.example.cyberopoli.ui.screens.scan.ScanScreen
import kotlinx.serialization.Serializable

sealed interface CyberopoliRoute {
    @Serializable data object Auth : CyberopoliRoute
    @Serializable data object Login : CyberopoliRoute
    @Serializable data object SignUp : CyberopoliRoute
    @Serializable data object Guest : CyberopoliRoute
    @Serializable data object Scan : CyberopoliRoute
}

@Composable
fun CyberopoliNavGraph(navController: NavHostController) {
    NavHost (
        navController = navController,
        startDestination = CyberopoliRoute.Auth
    ) {
        composable<CyberopoliRoute.Auth> {
            AuthScreen(navController)
        }
        composable<CyberopoliRoute.Login> {
            LoginScreen(navController)
        }
        composable<CyberopoliRoute.SignUp> {
            SignUpScreen(navController)
        }
        composable<CyberopoliRoute.Guest> {
            GuestScreen(navController)
        }
        composable<CyberopoliRoute.Scan> {
            ScanScreen(navController)
        }
    }
}