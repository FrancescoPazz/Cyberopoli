package com.example.cyberopoli.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cyberopoli.data.models.Theme
import com.example.cyberopoli.ui.screens.ar.ARScreen
import com.example.cyberopoli.ui.screens.auth.AuthScreen
import com.example.cyberopoli.ui.screens.auth.AuthState
import com.example.cyberopoli.ui.screens.auth.AuthViewModel
import com.example.cyberopoli.ui.screens.auth.GuestScreen
import com.example.cyberopoli.ui.screens.auth.LoginScreen
import com.example.cyberopoli.ui.screens.auth.SignUpScreen
import com.example.cyberopoli.ui.screens.home.HomeScreen
import com.example.cyberopoli.ui.screens.scan.ScanScreen
import com.example.cyberopoli.ui.screens.settings.SettingScreen
import com.example.cyberopoli.ui.screens.settings.SettingsViewModel
import com.example.cyberopoli.ui.theme.CyberopoliTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface CyberopoliRoute {
    @Serializable
    data object Auth : CyberopoliRoute
    @Serializable
    data object Login : CyberopoliRoute
    @Serializable
    data object SignUp : CyberopoliRoute
    @Serializable
    data object Guest : CyberopoliRoute
    @Serializable
    data object Scan : CyberopoliRoute
    @Serializable
    data object ARScreen : CyberopoliRoute
    @Serializable
    data object Settings : CyberopoliRoute
    @Serializable
    data object Home : CyberopoliRoute
}

@Composable
fun CyberopoliNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val themeState by settingsViewModel.state.collectAsStateWithLifecycle()
    val authState = authViewModel.authState.observeAsState()


    CyberopoliTheme(
        darkTheme = when (themeState.theme) {
            Theme.Light -> false
            Theme.Dark -> true
            Theme.System -> isSystemInDarkTheme()
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (authState.value == AuthState.Authenticated) CyberopoliRoute.Home else CyberopoliRoute.Auth,
        ) {
            composable<CyberopoliRoute.Auth> {
                AuthScreen(navController)
            }
            composable<CyberopoliRoute.Login> {
                LoginScreen(navController, authViewModel)
            }
            composable<CyberopoliRoute.SignUp> {
                SignUpScreen(navController, authViewModel)
            }
            composable<CyberopoliRoute.Guest> {
                GuestScreen(navController)
            }
            composable<CyberopoliRoute.Scan> {
                ScanScreen(navController, authViewModel)
            }
            composable<CyberopoliRoute.ARScreen> {
                ARScreen(navController)
            }
            composable<CyberopoliRoute.Settings> {
                SettingScreen(
                    navController,
                    themeState,
                    settingsViewModel::changeTheme,
                    authViewModel
                )
            }
            composable<CyberopoliRoute.Home> {
                HomeScreen(navController)
            }
        }
    }

}