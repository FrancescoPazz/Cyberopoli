package com.example.cyberopoli.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.CyberopoliRoute
import com.example.cyberopoli.ui.composables.auth.AuthButton
import com.example.cyberopoli.ui.composables.auth.AuthCard
import com.example.cyberopoli.ui.composables.auth.AuthOutlinedTextField

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    val username = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val birthDate = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate(CyberopoliRoute.Home) {
                    popUpTo(CyberopoliRoute.SignUp) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            is AuthState.Error -> Toast.makeText(
                context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    AuthCard(navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthOutlinedTextField(
                value = username,
                placeholder = stringResource(R.string.username),
            )

            AuthOutlinedTextField(
                value = name,
                placeholder = stringResource(R.string.name),
            )
            AuthOutlinedTextField(
                value = lastName,
                placeholder = stringResource(R.string.last_name),
            )

            AuthOutlinedTextField(
                value = birthDate,
                placeholder = stringResource(R.string.birth_date),
            )

            AuthOutlinedTextField(
                value = email,
                placeholder = stringResource(R.string.email),
            )

            AuthOutlinedTextField(
                value = password,
                placeholder = stringResource(R.string.password),
                visualTransformation = PasswordVisualTransformation(),
            )

            AuthOutlinedTextField(
                value = passwordConfirm,
                placeholder = stringResource(R.string.password_confirm),
                visualTransformation = PasswordVisualTransformation(),
            )

            AuthButton(
                text = stringResource(R.string.enter).uppercase(),
                onClick = {
                    authViewModel.signUp(context, email.value, password.value)
                },
                enabled = authState.value != AuthState.Loading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
