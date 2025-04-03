package com.example.cyberopoli.ui.composables.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.screens.auth.AuthState
import com.example.cyberopoli.ui.screens.auth.AuthViewModel

@Composable
fun SignUpCard(
    authViewModel: AuthViewModel,
) {
    var name = remember { mutableStateOf("") }
    var surname = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AuthOutlinedTextField(
            value = name,
            placeholder = stringResource(R.string.name),
            imageVector = Icons.Default.PermIdentity,
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        AuthOutlinedTextField(
            value = surname,
            placeholder = stringResource(R.string.last_name),
            imageVector = Icons.Default.PermIdentity,
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            text = stringResource(R.string.signup).uppercase(),
            onClick = {
                authViewModel.signUp(context, email.value, password.value)
            },
            enabled = authState.value != AuthState.Loading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
