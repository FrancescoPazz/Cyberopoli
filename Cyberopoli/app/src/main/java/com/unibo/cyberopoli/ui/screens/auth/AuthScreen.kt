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
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.BottomBar // Assicurati che queste usino il tema
import com.unibo.cyberopoli.ui.components.TopBar // Assicurati che queste usino il tema
import com.unibo.cyberopoli.ui.screens.auth.composables.AuthHeader
import com.unibo.cyberopoli.ui.screens.auth.composables.GuestCard
import com.unibo.cyberopoli.ui.screens.auth.composables.LoginCard
import com.unibo.cyberopoli.ui.screens.auth.composables.SignUpCard

@Composable
fun AuthScreen(navController: NavController, authParams: AuthParams) {
    val tabs = listOf(
        stringResource(R.string.login),
        stringResource(R.string.signup),
        stringResource(R.string.guest)
    )
    val context = LocalContext.current
    val authState = authParams.authState.observeAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Error) {
            Toast
                .makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_LONG)
                .show()
        }
        // Puoi aggiungere qui una logica per navigare automaticamente
        // se lo stato diventa AuthState.Authenticated
        // if (authState.value is AuthState.Authenticated) {
        //     navController.navigate(CyberopoliRoute.Home) {
        //         popUpTo(navController.graph.startDestinationId) { inclusive = true }
        //         launchSingleTop = true
        //     }
        // }
    }
    Scaffold(
        topBar = { TopBar(navController) },
        // Mostra la BottomBar solo se autenticato
        bottomBar = {
            if (authState.value == AuthState.Authenticated) {
                BottomBar(navController)
            }
        },
        // Il colore dello Scaffold (sfondo della schermata)
        containerColor = MaterialTheme.colorScheme.background // Usa background per lo sfondo principale
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AuthHeader userà i colori del tema internamente
            AuthHeader()

            TabRow(selectedTabIndex = selectedTabIndex,
                // Colore di sfondo della TabRow (trasparente va bene sullo sfondo)
                containerColor = Color.Transparent,
                // contentColor qui imposta il colore DEFAULT per gli item NON selezionati.
                // Tuttavia, possiamo sovrascriverlo esplicitamente nei singoli Tab.
                // Lo impostiamo su onSurfaceVariant per un colore non selezionato meno prominente.
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant, // *** MODIFICATO: Usa onSurfaceVariant ***
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        // Il colore dell'indicatore dovrebbe corrispondere al colore del tab selezionato.
                        color = MaterialTheme.colorScheme.primary // *** MODIFICATO: Usa primary per l'indicatore ***
                    )
                }) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        // Definisci esplicitamente i colori di testo per i tab selezionati/non selezionati
                        selectedContentColor = MaterialTheme.colorScheme.primary, // *** AGGIUNTO: Usa primary per il testo selezionato ***
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant, // *** AGGIUNTO: Usa onSurfaceVariant per il testo non selezionato ***
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> LoginCard(
                    authParams.authState.observeAsState(),
                    authParams.login,
                    authParams.loginGoogleUser,
                    authParams.resetPassword
                )
                1 -> SignUpCard(
                    navController, authParams.authState.observeAsState(), authParams.signUp
                )
                2 -> GuestCard(navController, authParams.loginAnonymously)
            }
        }
    }
}