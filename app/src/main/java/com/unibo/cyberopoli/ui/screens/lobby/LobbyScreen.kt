package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.unibo.cyberopoli.ui.composables.lobby.PlayerRow
import com.unibo.cyberopoli.ui.contracts.LobbyParams
import com.unibo.cyberopoli.util.PermissionHandler
import com.unibo.cyberopoli.util.UsageStatsHelper

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    lobbyParams: LobbyParams
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity

    LaunchedEffect(Unit) {
        Log.d("LobbyScreen", "Scanned value: ${lobbyParams.lobby.value?.matchId}")
        lobbyParams.joinLobby(lobbyParams.scannedLobbyId, lobbyParams.playerName)
        lobbyParams.observeLobby(lobbyParams.scannedLobbyId)

        // Well being Permission logic
        val permissionHandler = PermissionHandler(activity)
        val usageStatsHelper = UsageStatsHelper(context)

        if (permissionHandler.hasUsageStatsPermission()) {
            usageStatsHelper.logUsageStats()
        } else {
            permissionHandler.requestUsageStatsPermission()
        }

        lobbyParams.joinLobby(lobbyParams.scannedLobbyId, lobbyParams.playerName)
        lobbyParams.observeLobby(lobbyParams.scannedLobbyId)
    }

    BackHandler {
        lobbyParams.leaveLobby(lobbyParams.scannedLobbyId)
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
            if (lobbyParams.lobby.value == null) {
                Text(
                    text = stringResource(R.string.lobby_loading),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val playersMap = lobbyParams.lobby.value!!.players
                val playersList = playersMap.entries.map { it.key to it.value }

                Column {
                    Text(text = "Giocatori (${playersList.size}/8) In attesa di altri giocatori...")

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(playersList) { (_, playerInfo) ->
                            PlayerRow(
                                playerName = playerInfo.name, isReady = playerInfo.ready
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    AuthButton(stringResource(R.string.ready), onClick = {
                        lobbyParams.toggleReady(lobbyParams.scannedLobbyId)
                    })

                    AuthButton(stringResource(R.string.exit), onClick = {
                        lobbyParams.leaveLobby(lobbyParams.scannedLobbyId)
                        navController.navigateUp()
                    })
                }
            }
        }
    }
}