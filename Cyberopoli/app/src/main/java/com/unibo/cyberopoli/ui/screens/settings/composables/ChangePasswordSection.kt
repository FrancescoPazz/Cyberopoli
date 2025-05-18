package com.unibo.cyberopoli.ui.screens.settings.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.unibo.cyberopoli.R

@Composable
fun ChangePasswordSection(
    currentPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.change_password),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        SettingsOutlinedTextField(
            value = currentPassword,
            onValueChange = onCurrentPasswordChange,
            label = stringResource(R.string.old_password),
            visualTransformation = PasswordVisualTransformation()
        )
        SettingsOutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = stringResource(R.string.password),
            visualTransformation = PasswordVisualTransformation()
        )
        SettingsOutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = stringResource(R.string.password_confirm),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = onChangeClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.tertiary,
                containerColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text(stringResource(R.string.change_password))
        }
    }
}