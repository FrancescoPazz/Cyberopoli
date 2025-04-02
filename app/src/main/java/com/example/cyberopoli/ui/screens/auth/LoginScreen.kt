package com.example.cyberopoli.ui.screens.auth

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.CyberopoliRoute
import com.example.cyberopoli.ui.composables.auth.AuthButton
import com.example.cyberopoli.ui.composables.auth.AuthOutlinedTextField
import com.example.cyberopoli.ui.composables.auth.AuthCard

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> {
                navController.navigate(CyberopoliRoute.Home) {
                    popUpTo(CyberopoliRoute.Login) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    AuthCard(navController = navController) {
        AuthOutlinedTextField(
            value = email,
            placeholder = stringResource(R.string.email),
        )

        AuthOutlinedTextField(
            value = password,
            placeholder = stringResource(R.string.password),
            visualTransformation = PasswordVisualTransformation(),
        )

        AuthButton(
            text = stringResource(R.string.enter).uppercase(),
            onClick = {
                authViewModel.login(context, email.value, password.value)
            },
            enabled = authState.value != AuthState.Loading
        )
    }
}