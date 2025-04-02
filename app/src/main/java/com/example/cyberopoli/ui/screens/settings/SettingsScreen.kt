package com.example.cyberopoli.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.data.models.Theme
import com.example.cyberopoli.ui.composables.BottomBar
import com.example.cyberopoli.ui.composables.TopBar
import com.example.cyberopoli.ui.screens.auth.AuthViewModel

@Composable
fun SettingScreen(
    navController: NavController,
    themeState: ThemeState,
    onThemeChange: (Theme) -> Unit,
    authViewModel: AuthViewModel
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar(navController, stringResource(R.string.settings)) },
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
                Theme.entries.forEach { theme ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = theme == themeState.theme,
                                onClick = {
                                    onThemeChange(theme)
                                  },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        RadioButton(
                            selected = theme == themeState.theme,
                            onClick = null
                        )
                        Text(
                            text = when (theme) {
                                Theme.Light -> stringResource(R.string.light)
                                Theme.Dark -> stringResource(R.string.dark)
                                Theme.System -> stringResource(R.string.system)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                HorizontalDivider()

                Text(text = stringResource(R.string.notifications), style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.enable_notifications))
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }

                HorizontalDivider()

                Text(text = stringResource(R.string.change_password), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(stringResource(R.string.old_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.password_confirm)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.change_password))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { authViewModel.logout() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    )
}