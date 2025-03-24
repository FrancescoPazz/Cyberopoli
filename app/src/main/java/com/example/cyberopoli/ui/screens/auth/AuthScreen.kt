package com.example.cyberopoli.ui.screens.auth

import android.icu.lang.UCharacter.toUpperCase
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.CyberopoliRoute
import com.example.cyberopoli.ui.composables.auth.AuthButton
import com.example.cyberopoli.ui.composables.auth.AuthTemplate

@Composable
fun AuthScreen(navController: NavController) {
    AuthTemplate (navController = navController) {
        AuthButton(
            text = toUpperCase(stringResource(R.string.login)),
            onClick = { navController.navigate(CyberopoliRoute.Login) }
        )
        AuthButton(
            text = toUpperCase(stringResource(R.string.signup)),
            onClick = { navController.navigate(CyberopoliRoute.SignUp) }
        )
        AuthButton(text = toUpperCase(stringResource(R.string.guest)),
            onClick = { navController.navigate(CyberopoliRoute.Guest) }
        )
    }
}