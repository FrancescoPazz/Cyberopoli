package com.example.cyberopoli.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cyberopoli.ui.screens.AuthScreen
import kotlinx.serialization.Serializable

sealed interface CyberopoliRoute {
    @Serializable data object Auth : CyberopoliRoute
    @Serializable data object Guest : CyberopoliRoute
    @Serializable data object Login : CyberopoliRoute
    @Serializable data object SignUp : CyberopoliRoute
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
    }
}