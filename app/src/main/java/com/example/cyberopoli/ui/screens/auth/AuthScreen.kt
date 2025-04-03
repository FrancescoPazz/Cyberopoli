package com.example.cyberopoli.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.ui.CyberopoliRoute
import com.example.cyberopoli.ui.composables.auth.AuthHeader
import com.example.cyberopoli.ui.composables.auth.GuestCard
import com.example.cyberopoli.ui.composables.auth.LoginCard
import com.example.cyberopoli.ui.composables.auth.SignUpCard

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Login", "Sign Up", "Guest")

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate(CyberopoliRoute.Home) {
                    popUpTo(CyberopoliRoute.Login) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthHeader()

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> LoginCard(authViewModel)
            1 -> SignUpCard(authViewModel)
            2 -> GuestCard(navController)
        }
    }
}
