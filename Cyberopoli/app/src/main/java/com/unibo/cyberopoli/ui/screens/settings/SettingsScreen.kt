package com.unibo.cyberopoli.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.settings.composables.ChangePasswordSection
import com.unibo.cyberopoli.ui.screens.settings.composables.LogoutButton
import com.unibo.cyberopoli.ui.screens.settings.composables.NotificationSection
import com.unibo.cyberopoli.ui.screens.settings.composables.ThemeSection

@Composable
fun SettingScreen(
    navController: NavController,
    settingsParams: SettingsParams
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            if (settingsParams.authState.value == AuthState.Authenticated) {
                BottomBar(navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ThemeSection(
                currentTheme = settingsParams.themeState.theme,
                onThemeSelected = { settingsParams.changeTheme(it) }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            NotificationSection(
                enabled = notificationsEnabled,
                onToggle = { notificationsEnabled = it }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            if (settingsParams.authState.value == AuthState.Authenticated) {
                ChangePasswordSection(
                    updatePasswordWithOldPassword = settingsParams.updatePasswordWithOldPassword,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LogoutButton(
                    onLogout = { settingsParams.logout() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}