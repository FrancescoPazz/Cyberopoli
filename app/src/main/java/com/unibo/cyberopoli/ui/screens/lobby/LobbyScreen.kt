package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.auth.composables.AuthButton
import com.unibo.cyberopoli.ui.screens.lobby.composables.PlayerRow
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController, lobbyParams: LobbyParams
) {
    Log.d("LobbyScreen", "LobbyParams: $lobbyParams")
    val lobbyId = UUID.nameUUIDFromBytes(lobbyParams.scannedLobbyId.toByteArray()).toString()
    Log.d("LobbyScreen", "LobbyId: $lobbyId")
    val playerName = lobbyParams.playerName
    Log.d("LobbyScreen", "LobbyId: $lobbyId, PlayerName: $playerName")

    if (lobbyId.isNotBlank()) {
        LaunchedEffect(lobbyId) {
            lobbyParams.startLobbyFlow(lobbyId)
        }
    }

    BackHandler {
        navController.navigateUp()
    }

    Scaffold(topBar = { TopBar(navController) },
        bottomBar = { if (!lobbyParams.isGuest) BottomBar(navController) }) { paddingValues ->
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
                val playersMap = lobbyParams.players?.associateBy { it.userId } ?: emptyMap()
                val playersList = playersMap.entries.map { it.key to it.value }

                Column {
                    Text(text = "Players (${playersList.size}/8) in lobby...")

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(playersList) { (_, playerInfo) ->
                            PlayerRow(
                                playerName = playerInfo.userId!!, isReady = playerInfo.isReady!!
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    AuthButton(text = stringResource(R.string.ready), onClick = {
                        lobbyParams.toggleReady()
                    })

                    AuthButton(stringResource(R.string.exit), onClick = {
                        lobbyParams.leaveLobby()
                        //lobbyParams.deleteAnonymousUserAndSignOut()
                        navController.navigate(CyberopoliRoute.Home) {
                            popUpTo(CyberopoliRoute.Home) {
                                inclusive = true
                            }
                        }
                    })
                }
            }
        }
    }
}