package com.unibo.cyberopoli.ui.screens.profile.view.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.CyberOutlinedTextField
import com.unibo.cyberopoli.ui.components.CyberopoliCard
import com.unibo.cyberopoli.ui.screens.settings.view.composables.ChangePasswordSection

@Composable
fun EditProfileSection(
    user: User,
    updateUserInfo: (String?, String?, () -> Unit, (String) -> Unit) -> Unit,
    updatePasswordWithOldPassword: (oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val newName = remember(user.name) { mutableStateOf(user.name ?: "") }
    val newSurname = remember(user.surname) { mutableStateOf(user.surname ?: "") }
    var showChangePasswordSection by remember { mutableStateOf(false) }
    var showEditProfileSection by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var isErrorDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            icon = if (isErrorDialog) {
                { Icon(Icons.Filled.Error, contentDescription = "Error Icon") }
            } else {
                { Icon(Icons.Filled.CheckCircle, contentDescription = "Success Icon") }
            },
        )
    }

    CyberopoliCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentPadding = 16.dp,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.edit_profile),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            OutlinedButton(
                onClick = { showEditProfileSection = !showEditProfileSection },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_profile),
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    if (showEditProfileSection) {
                        stringResource(R.string.hide)
                    } else {
                        stringResource(R.string.edit_profile)
                    },
                )
            }

            if (showEditProfileSection) {
                Spacer(modifier = Modifier.height(16.dp))

                CyberOutlinedTextField(
                    value = newName,
                    placeholder = stringResource(R.string.name),
                    imageVector = Icons.Default.Person,
                    singleLine = true,
                )

                CyberOutlinedTextField(
                    value = newSurname,
                    placeholder = stringResource(R.string.last_name),
                    imageVector = Icons.Default.AccountCircle,
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        updateUserInfo(newName.value, newSurname.value, {
                            dialogTitle = context.getString(R.string.change_success)
                            dialogMessage = context.getString(R.string.change_profile_success)
                            isErrorDialog = false
                            showDialog = true
                            showEditProfileSection = false
                        }, {
                            dialogTitle = context.getString(R.string.change_fail)
                            dialogMessage = context.getString(R.string.change_profile_failed)
                            isErrorDialog = true
                            showDialog = true
                        })
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.save_changes),
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.save_changes))
                }
            }

            OutlinedButton(
                onClick = { showChangePasswordSection = !showChangePasswordSection },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = stringResource(R.string.change_password),
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    if (showChangePasswordSection) {
                        stringResource(R.string.hide)
                    } else {
                        stringResource(R.string.change_password)
                    },
                )
            }

            if (showChangePasswordSection) {
                Spacer(modifier = Modifier.height(8.dp))
                ChangePasswordSection(
                    updatePasswordWithOldPassword = { oldPass, newPass, _, _ ->
                        updatePasswordWithOldPassword(oldPass, newPass, {
                            dialogTitle = context.getString(R.string.change_success)
                            dialogMessage = context.getString(R.string.change_password_success)
                            isErrorDialog = false
                            showDialog = true
                            showChangePasswordSection = false
                        }, {
                            dialogTitle = context.getString(R.string.change_fail)
                            dialogMessage = context.getString(R.string.change_password_failed)
                            isErrorDialog = true
                            showDialog = true
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
