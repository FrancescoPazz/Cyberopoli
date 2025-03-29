package com.example.cyberopoli.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.CyberopoliRoute
import com.example.cyberopoli.ui.composables.auth.AuthButton
import com.example.cyberopoli.ui.composables.auth.AuthOutlinedTextField
import com.example.cyberopoli.ui.composables.auth.AuthCard

@Composable
fun GuestScreen(navController: NavController) {
    val username = remember { mutableStateOf("") }

    AuthCard(navController) {
        AuthOutlinedTextField(
            value = username,
            placeholder = stringResource(R.string.username),
        )
        AuthButton(
            text = stringResource(R.string.enter).uppercase(),
            onClick = {
                if (username.value.isNotEmpty())
                    navController.navigate(CyberopoliRoute.Home) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}