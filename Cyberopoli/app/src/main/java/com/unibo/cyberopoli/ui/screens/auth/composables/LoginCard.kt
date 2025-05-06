package com.unibo.cyberopoli.ui.screens.auth.composables

import android.content.Context
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

@Composable
fun LoginCard(
    authState: State<AuthState?>,
    login: (email: String, password: String) -> Unit,
    googleLogin: (context: Context) -> Unit,
    resetPassword: (email: String) -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isResetMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isResetMode) {
            AuthOutlinedTextField(
                value = email,
                placeholder = stringResource(R.string.email),
                imageVector = Icons.Default.Email,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthOutlinedTextField(
                value = password,
                placeholder = stringResource(R.string.password),
                imageVector = Icons.Default.Lock,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )

            AuthButton(
                text = stringResource(R.string.login).uppercase(), onClick = {
                    login(email.value, password.value)
                }, enabled = authState.value != AuthState.Loading
            )

            TextButton(
                onClick = { isResetMode = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Forgot password?")
            }

            val context = LocalContext.current
            GoogleSignInButton(
                onClick = { googleLogin(context) },
            )
        } else {
            Text(
                text = stringResource(R.string.reset_password_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(16.dp))
            AuthOutlinedTextField(
                value = email,
                placeholder = stringResource(R.string.email),
                imageVector = Icons.Default.Email,
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            AuthButton(
                text = stringResource(R.string.send_reset_email), onClick = {
                    resetPassword(email.value.trim())
                }, enabled = email.value.isNotBlank() && authState.value != AuthState.Loading
            )
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = { isResetMode = false }, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(stringResource(R.string.back_to_login))
            }
        }
    }
}
