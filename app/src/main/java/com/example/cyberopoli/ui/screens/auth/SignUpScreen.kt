package com.example.cyberopoli.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.composables.auth.AuthButton
import com.example.cyberopoli.ui.composables.auth.AuthOutlinedTextField
import com.example.cyberopoli.ui.composables.auth.AuthTemplate

@Composable
fun SignUpScreen(navController: NavController) {
    val username = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val birthDate = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }

    AuthTemplate(navController = navController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
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
                    // TODO
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
