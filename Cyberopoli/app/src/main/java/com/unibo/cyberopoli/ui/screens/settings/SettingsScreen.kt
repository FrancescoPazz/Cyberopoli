package com.unibo.cyberopoli.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.data.models.settings.CyberopoliInstructions
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.settings.composables.LogoutButton
import com.unibo.cyberopoli.ui.screens.settings.composables.ThemeSection

@Composable
fun SettingScreen(
    navController: NavController,
    settingsParams: SettingsParams,
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var currentRulePage by remember { mutableIntStateOf(0) }

    val rulesPages = CyberopoliInstructions(context)

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_confirm)) },
            text = { Text(stringResource(R.string.logout_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    settingsParams.logout()
                    showLogoutDialog = false
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = {
                showRulesDialog = false
                currentRulePage = 0
            },
            title = { Text(rulesPages[currentRulePage].first) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = rulesPages[currentRulePage].second,
                        textAlign = TextAlign.Start
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(rulesPages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(8.dp)
                                    .background(
                                        color = if (index == currentRulePage)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (currentRulePage < rulesPages.size - 1) {
                            currentRulePage++
                        } else {
                            showRulesDialog = false
                            currentRulePage = 0
                        }
                    }
                ) {
                    Text(
                        if (currentRulePage < rulesPages.size - 1)
                            stringResource(R.string.next)
                        else
                            stringResource(R.string.close)
                    )
                }
            },
            dismissButton = {
                if (currentRulePage > 0) {
                    TextButton(onClick = { currentRulePage-- }) {
                        Text(stringResource(R.string.back))
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            if (settingsParams.authState.value == AuthState.Authenticated) {
                BottomBar(navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ThemeSection(
                currentTheme = settingsParams.themeState.theme,
                onThemeSelected = { settingsParams.changeTheme(it) },
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Button(
                onClick = { showRulesDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.game_instructions))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            if (settingsParams.authState.value == AuthState.Authenticated) {
                LogoutButton(
                    onLogout = { showLogoutDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}