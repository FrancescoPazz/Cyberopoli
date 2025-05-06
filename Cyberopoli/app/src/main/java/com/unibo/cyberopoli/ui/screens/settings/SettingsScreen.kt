package com.unibo.cyberopoli.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.theme.Theme
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.data.models.auth.AuthState
@Composable
fun SettingScreen(
    navController: NavController,
    settingsParams: SettingsParams,
) {
    val context = LocalContext.current

    var notificationsEnabled by remember { mutableStateOf(true) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(topBar = { TopBar(navController) }, bottomBar = {
        if (settingsParams.authState.value == AuthState.Authenticated) BottomBar(navController)
    }, content = { paddingValues ->
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = theme == settingsParams.themeState.theme, onClick = {
                                settingsParams.changeTheme(theme)
                            }, role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    RadioButton(
                        selected = theme == settingsParams.themeState.theme,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                        )
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

            Text(
                text = stringResource(R.string.notifications),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.enable_notifications))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onTertiary,
                        checkedTrackColor = MaterialTheme.colorScheme.onBackground,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            HorizontalDivider()

            if (settingsParams.authState.value == AuthState.Authenticated) {
                Text(
                    text = stringResource(R.string.change_password),
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(stringResource(R.string.old_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    )
                )
                OutlinedTextField(value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    )
                )
                OutlinedTextField(value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.password_confirm)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    )
                )
                Button(
                    onClick = {
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
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.onTertiary
                    ),
                ) {
                    Text(stringResource(R.string.change_password))
                }
                Button(
                    onClick = { settingsParams.logout() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.onError
                    ),
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    })
}