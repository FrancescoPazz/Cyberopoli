package com.unibo.cyberopoli.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.settings.composables.ChangePasswordSection
import com.unibo.cyberopoli.ui.screens.settings.composables.LogoutButton
import com.unibo.cyberopoli.ui.screens.settings.composables.NotificationSection
import com.unibo.cyberopoli.ui.screens.settings.composables.ThemeSection

@Composable
fun SettingScreen(
    navController: NavController, settingsParams: SettingsParams
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(topBar = { TopBar(navController) }, bottomBar = {
        if (settingsParams.authState.value == AuthState.Authenticated) {
            BottomBar(navController)
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ThemeSection(currentTheme = settingsParams.themeState.theme,
                onThemeSelected = { settingsParams.changeTheme(it) })

            HorizontalDivider()

            NotificationSection(enabled = notificationsEnabled,
                onToggle = { notificationsEnabled = it })

            HorizontalDivider()

            if (settingsParams.authState.value == AuthState.Authenticated) {
                ChangePasswordSection(currentPassword = currentPassword,
                    onCurrentPasswordChange = { currentPassword = it },
                    newPassword = newPassword,
                    onNewPasswordChange = { newPassword = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    onChangeClick = {
                        if (newPassword == confirmPassword) {
                            settingsParams.updatePasswordWithOldPassword(currentPassword,
                                newPassword,
                                {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.password_change_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.password_change_fail),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.password_not_match),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                LogoutButton(onLogout = { settingsParams.logout() })
            }
        }
    }
}