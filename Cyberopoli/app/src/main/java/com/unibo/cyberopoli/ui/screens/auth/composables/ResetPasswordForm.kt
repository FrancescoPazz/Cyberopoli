package com.unibo.cyberopoli.ui.screens.auth.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.CyberOutlinedTextField

@Composable
fun ResetPasswordForm(
    email: MutableState<String>,
    onEmailChange: (String) -> Unit,
    showOtpFields: Boolean,
    otp: MutableState<String>,
    onOtpChange: (String) -> Unit,
    newPassword: MutableState<String>,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: MutableState<String>,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSendResetEmail: () -> Unit,
    changeForgottenPassword: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.reset_password_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))

        CyberOutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.email),
            imageVector = Icons.Default.Email,
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        AuthButton(
            text = stringResource(R.string.send_reset_email),
            onClick = onSendResetEmail,
            enabled = email.value.isNotBlank() && !isLoading
        )

        if (showOtpFields) {
            Spacer(Modifier.height(24.dp))

            CyberOutlinedTextField(
                value = otp,
                onValueChange = onOtpChange,
                placeholder = stringResource(R.string.enter_otp),
                imageVector = Icons.Default.LockReset,
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            CyberOutlinedTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                placeholder = stringResource(R.string.new_password),
                imageVector = Icons.Default.Lock,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(8.dp))
            CyberOutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = stringResource(R.string.password_confirm),
                imageVector = Icons.Default.Lock,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(8.dp))
            AuthButton(
                text = stringResource(R.string.reset_password_title).uppercase(),
                onClick = {
                    changeForgottenPassword()
                },
                enabled = otp.value.isNotBlank() && newPassword.value.isNotBlank() && newPassword.value == confirmPassword.value && !isLoading
            )
        }

        Spacer(Modifier.height(16.dp))
        TextButton(
            onClick = onBack, colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            ), modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.back_to_login))
        }
    }
}
