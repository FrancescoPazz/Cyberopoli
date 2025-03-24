package com.example.cyberopoli.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.composables.auth.AuthButton
import com.example.cyberopoli.ui.composables.auth.AuthOutlinedTextField
import com.example.cyberopoli.ui.composables.auth.AuthTemplate

@Composable
fun LoginScreen(navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    AuthTemplate(navController = navController) {
        AuthOutlinedTextField(
            value = username,
            placeholder = stringResource(R.string.username),
        )

        AuthOutlinedTextField(
            value = password,
            placeholder = stringResource(R.string.password),
            visualTransformation = PasswordVisualTransformation(),
        )

        AuthButton(
            text = stringResource(R.string.enter).uppercase(),
            onClick = {
                // TODO
            }
        )
    }
}