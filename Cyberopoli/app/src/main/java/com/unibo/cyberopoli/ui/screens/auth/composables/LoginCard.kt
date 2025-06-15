package com.unibo.cyberopoli.ui.screens.auth.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.CyberOutlinedTextField

@Composable
fun LoginCard(
    authState: State<AuthState?>,
    login: (email: String, password: String) -> Unit,
    googleLogin: (context: Context) -> Unit,
    sendResetEmail: (email: String) -> Unit,
    sendOtp: (email: String, otp: String, newPassword: String) -> Unit,
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isResetMode by remember { mutableStateOf(false) }
    val otp = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!isResetMode) {
            CyberOutlinedTextField(
                value = email,
                onValueChange = { email.value = it },
                placeholder = stringResource(R.string.email),
                imageVector = Icons.Default.Email,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            CyberOutlinedTextField(
                value = password,
                onValueChange = { password.value = it },
                placeholder = stringResource(R.string.password),
                imageVector = Icons.Default.Lock,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )

            AuthButton(
                text = stringResource(R.string.login).uppercase(),
                onClick = { login(email.value.trim(), password.value) },
                enabled = authState.value != AuthState.Loading,
            )

            TextButton(
                onClick = { isResetMode = true },
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                    ),
            ) {
                Text(stringResource(R.string.forgot_password))
            }

            GoogleSignInButton(onClick = { googleLogin(context) })
        } else {
            ResetPasswordForm(
                email = email,
                onEmailChange = { email.value = it },
                otp = otp,
                onOtpChange = {
                    otp.value = it
                },
                newPassword = newPassword,
                onNewPasswordChange = {
                    newPassword.value = it
                },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = {
                    confirmPassword.value = it
                },
                isLoading = authState.value == AuthState.Loading,
                onSendResetEmail = {
                    sendResetEmail(email.value.trim())
                },
                sendOtp = {
                    sendOtp(
                        email.value.trim(),
                        otp.value.trim(),
                        newPassword.value.trim(),
                    )
                },
                onBack = { isResetMode = false },
            )
        }
    }
}
