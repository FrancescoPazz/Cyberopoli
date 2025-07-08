package com.unibo.cyberopoli.ui.screens.auth.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.CyberOutlinedTextField

@Composable
fun SignUpCard(
    navController: NavController,
    authState: State<AuthState?>,
    signUp: (
        name: String?,
        surname: String?,
        username: String,
        email: String,
        password: String,
    ) -> Unit,
) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val passwordsMatch =
        password.value.isNotBlank() &&
            password.value == confirmPassword.value

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CyberOutlinedTextField(
            value = name,
            placeholder = stringResource(R.string.name),
            imageVector = Icons.Default.Person,
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = surname,
            placeholder = stringResource(R.string.last_name),
            imageVector = Icons.Default.Person,
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = username,
            placeholder = stringResource(R.string.username),
            imageVector = Icons.Default.Face,
            singleLine = true,
            isRequired = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = email,
            placeholder = stringResource(R.string.email),
            imageVector = Icons.Default.Email,
            singleLine = true,
            isRequired = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = password,
            placeholder = stringResource(R.string.password),
            imageVector = Icons.Default.Lock,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isRequired = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = confirmPassword,
            placeholder = stringResource(R.string.password_confirm),
            imageVector = Icons.Default.Lock,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isRequired = true,
        )

        AuthButton(
            text = stringResource(R.string.signup).uppercase(),
            onClick = {
                signUp(
                    name.value,
                    surname.value,
                    username.value,
                    email.value,
                    password.value,
                )
            },
            enabled = authState.value != AuthState.Loading && passwordsMatch,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
