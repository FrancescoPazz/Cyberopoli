package com.unibo.cyberopoli.ui.screens.auth.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.unibo.cyberopoli.data.models.auth.AuthErrorContext
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.auth.viewmodel.AuthParams
import com.unibo.cyberopoli.ui.screens.auth.view.composables.AuthHeader
import com.unibo.cyberopoli.ui.screens.auth.view.composables.GuestCard
import com.unibo.cyberopoli.ui.screens.auth.view.composables.LoginCard
import com.unibo.cyberopoli.ui.screens.auth.view.composables.SignUpCard

@SuppressLint("DiscouragedApi")
@Composable
fun AuthScreen(
    navController: NavController,
    authParams: AuthParams,
) {
    val tabs =
        listOf(
            stringResource(R.string.login),
            stringResource(R.string.signup),
            stringResource(R.string.guest),
        )
    val context = LocalContext.current
    val authState = authParams.authState
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val signupSuccess = stringResource(R.string.signup_success)

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Error -> {
                val dialogId = state.message.split(" ")[0]
                val dialogMessage = context.getString(
                    context.resources.getIdentifier(
                        dialogId,
                        "string",
                        context.packageName
                    )
                )
                if (dialogMessage.isNotEmpty()) {
                    Toast.makeText(context, dialogMessage, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }

                when (state.context) {
                    AuthErrorContext.SIGNUP -> selectedTabIndex = 1
                    AuthErrorContext.LOGIN -> selectedTabIndex = 0
                    AuthErrorContext.ANONYMOUS_LOGIN -> selectedTabIndex = 2
                    else -> { }
                }
            }
            is AuthState.RegistrationSuccess -> {
                Toast.makeText(context, signupSuccess, Toast.LENGTH_LONG).show()
                selectedTabIndex = 0
            }
            else -> { }
        }
    }
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            if (authState.value == AuthState.Authenticated) {
                BottomBar(navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthHeader()

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = { Text(title) },
                    )
                }
            }
            when (selectedTabIndex) {
                0 ->
                    LoginCard(
                        authParams.authState,
                        authParams.login,
                        authParams.loginGoogleUser,
                        authParams.sendPasswordReset,
                        authParams.sendOtp,
                    )
                1 ->
                    SignUpCard(
                        navController,
                        authParams.authState,
                        authParams.signUp,
                    )
                2 -> GuestCard(navController, authParams.loginAnonymously)
            }
        }
    }
}
