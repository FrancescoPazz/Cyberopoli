package com.example.cyberopoli.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cyberopoli.ui.screens.ar.ARScreen
import com.example.cyberopoli.ui.screens.auth.AuthScreen
import com.example.cyberopoli.ui.screens.auth.GuestScreen
import com.example.cyberopoli.ui.screens.auth.LoginScreen
import com.example.cyberopoli.ui.screens.auth.SignUpScreen
import com.example.cyberopoli.ui.screens.home.HomeScreen
import com.example.cyberopoli.ui.screens.scan.ScanScreen
import com.example.cyberopoli.ui.screens.settings.SettingScreen
import com.example.cyberopoli.ui.screens.settings.Theme
import com.example.cyberopoli.ui.screens.settings.ThemeState
import kotlinx.serialization.Serializable

sealed interface CyberopoliRoute {
    @Serializable data object Auth : CyberopoliRoute
    @Serializable data object Login : CyberopoliRoute
    @Serializable data object SignUp : CyberopoliRoute
    @Serializable data object Guest : CyberopoliRoute
    @Serializable data object Scan : CyberopoliRoute
    @Serializable data object ARScreen : CyberopoliRoute
    @Serializable data object Settings : CyberopoliRoute
    @Serializable data object Home : CyberopoliRoute
}

@Composable
fun CyberopoliNavGraph(navController: NavHostController,
                       themeState: ThemeState,
                       onThemeChange: (Theme) -> Unit = {}) {
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
        composable<CyberopoliRoute.ARScreen> {
            ARScreen(navController)
        }
        composable<CyberopoliRoute.Settings> {
            SettingScreen(navController, themeState, onThemeChange)
        }
        composable<CyberopoliRoute.Home> {
            HomeScreen(navController)
        }
    }
}