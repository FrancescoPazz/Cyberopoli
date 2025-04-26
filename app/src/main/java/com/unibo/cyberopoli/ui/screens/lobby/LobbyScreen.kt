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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.screens.auth.composables.AuthButton
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.composables.PlayerRow
import kotlinx.coroutines.delay
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    lobbyParams: LobbyParams
) {
    var hasJoinedLobby by remember { mutableStateOf(false) }
    val shouldLeaveLobby by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val lobbyId = remember(lobbyParams.scannedLobbyId) {
        UUID.nameUUIDFromBytes(lobbyParams.scannedLobbyId.toByteArray()).toString()
    }
    val playerName = lobbyParams.playerName

    Log.d("LobbyScreen", "LobbyParams: $lobbyParams")
    Log.d("LobbyScreen", "LobbyId: $lobbyId")
    Log.d("LobbyScreen", "LobbyId: $lobbyId, PlayerName: $playerName")

    LaunchedEffect(lobbyId) {
        if (!hasJoinedLobby && lobbyId.isNotBlank()) {
            Log.d("LobbyScreen", "Starting lobby flow...")
            lobbyParams.startLobbyFlow(lobbyId)
            hasJoinedLobby = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY && hasJoinedLobby) {
                Log.d("LobbyScreen", "Leaving lobby...")
                lobbyParams.leaveLobby()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(shouldLeaveLobby) {
        if (shouldLeaveLobby) {
            delay(300)
            lobbyParams.leaveLobby()
        }
    }

    BackHandler {
        lobbyParams.leaveLobby()
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopBar(navController = navController, onBackPressed = {
                lobbyParams.leaveLobby()
                navController.popBackStack()
            })
        },
        bottomBar = { if (!lobbyParams.isGuest) BottomBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (lobbyParams.lobby.value == null) {
                LoadingScreen()
            } else {
                val playersList = lobbyParams.players.value.map { it.userId to it }

                Column {
                    Text(text = "Players (${playersList.size}/8) in lobby...")

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(playersList) { (_, playerInfo) ->
                            PlayerRow(
                                playerName = playerInfo.displayName ?: playerInfo.userId ?: "Unknown",
                                isReady = playerInfo.isReady ?: false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    AuthButton(text = stringResource(R.string.ready), onClick = {
                        lobbyParams.toggleReady()
                    })

                    AuthButton(stringResource(R.string.exit), onClick = {
                        lobbyParams.leaveLobby()
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}