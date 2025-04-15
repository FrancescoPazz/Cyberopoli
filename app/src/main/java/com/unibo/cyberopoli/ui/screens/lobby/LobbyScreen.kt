package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.composables.BottomBar
import com.unibo.cyberopoli.ui.composables.TopBar
import com.unibo.cyberopoli.ui.composables.auth.AuthButton
import com.unibo.cyberopoli.ui.screens.profile.ProfileViewModel
import com.unibo.cyberopoli.util.PermissionHandler
import com.unibo.cyberopoli.util.UsageStatsHelper

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    lobbyId: String,
    lobbyViewModel: LobbyViewModel,
    profileViewModel: ProfileViewModel,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity

    val currentLobby by lobbyViewModel.lobby.observeAsState()
    val userData by profileViewModel.user.observeAsState()
    val playerName = "${userData?.name} ${userData?.surname}"

    LaunchedEffect(Unit) {
        lobbyViewModel.joinLobby(lobbyId, playerName)
        lobbyViewModel.observeLobby(lobbyId)

        // Well being Permission logic
        val permissionHandler = PermissionHandler(activity)
        val usageStatsHelper = UsageStatsHelper(context)

        if (permissionHandler.hasUsageStatsPermission()) {
            usageStatsHelper.logUsageStats()
        } else {
            permissionHandler.requestUsageStatsPermission()
        }

        lobbyViewModel.joinLobby(lobbyId, playerName)
        lobbyViewModel.observeLobby(lobbyId)
    }

    BackHandler {
        lobbyViewModel.leaveLobby(lobbyId)
        navController.navigateUp()
    }

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (currentLobby == null) {
                Text(
                    text = stringResource(R.string.lobby_loading),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val playersMap = currentLobby!!.players
                val playersList = playersMap.entries.map { it.key to it.value }

                // Se vuoi implementare un countdown fittizio
                var secondsLeft by remember { mutableIntStateOf(150) } // 2:30
                // Potresti avere un timer che decrementa e, se vuoi, sincronizzare col DB
                // Ma qui lo facciamo localmente in composable
                LaunchedEffect(currentLobby) {
                    // Avvia un timer if needed
                }

                Column {
                    Text(text = "Inizio partita in $secondsLeft secondi")
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Giocatori (${playersList.size}/8) In attesa di altri giocatori...")

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(playersList) { (uid, playerInfo) ->
                            PlayerRow(
                                playerName = playerInfo.name, isReady = playerInfo.ready
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    AuthButton(stringResource(R.string.ready), onClick = {
                        lobbyViewModel.toggleReady(lobbyId)
                    })

                    AuthButton(stringResource(R.string.exit), onClick = {
                        lobbyViewModel.leaveLobby(lobbyId)
                        navController.navigateUp()
                    })
                }
            }
        }
    }
}

@Composable
fun PlayerRow(playerName: String, isReady: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = playerName)
        if (isReady) {
            Text(text = stringResource(R.string.ready))
        } else {
            Text(text = stringResource(R.string.waiting))
        }
    }
}
