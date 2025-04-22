package com.unibo.cyberopoli.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.CyberopoliRoute
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.auth.AuthHeader
import com.unibo.cyberopoli.ui.composables.auth.GuestCard
import com.unibo.cyberopoli.ui.composables.auth.LoginCard
import com.unibo.cyberopoli.ui.composables.auth.SignUpCard
import com.unibo.cyberopoli.ui.contracts.AuthParams
import com.unibo.cyberopoli.ui.contracts.AuthState

@Composable
fun AuthScreen(navController: NavController, authParams: AuthParams) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.login),
        stringResource(R.string.signup),
        stringResource(R.string.guest)
    )

    val authState = authParams.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate(CyberopoliRoute.Home) {
                    popUpTo(CyberopoliRoute.Auth) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            is AuthState.Anonymous -> {
                navController.navigate(CyberopoliRoute.Scan) {
                    popUpTo(CyberopoliRoute.Auth) {
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

    Scaffold(topBar = { TopBar(navController) }, bottomBar = {
        if (authState.value == AuthState.Authenticated) BottomBar(navController)
    }, content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthHeader()

            TabRow(selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.tertiary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) })
                }
            }

            when (selectedTabIndex) {
                0 -> LoginCard(
                    authParams.authState.observeAsState(),
                    authParams.login,
                    authParams.resetPassword
                )

                1 -> SignUpCard(authParams.authState.observeAsState(), authParams.signUp)
                2 -> GuestCard(navController, authParams.signInAnonymously)
            }
        }
    })
}
