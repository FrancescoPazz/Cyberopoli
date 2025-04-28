package com.unibo.cyberopoli.ui.screens.lobby

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.composables.PlayerRow

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController,
    params: LobbyParams
) {
    var hasJoined by remember { mutableStateOf(false) }

    LaunchedEffect(params.scannedLobbyId) {
        if (!hasJoined && params.scannedLobbyId.isNotBlank()) {
            params.startLobbyFlow(params.scannedLobbyId)
            hasJoined = true
        }
    }

    BackHandler {
        params.leaveLobby()
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopBar(navController) {
                params.leaveLobby()
                navController.popBackStack()
            }
        },
        bottomBar = { if (!params.isGuest) BottomBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (params.lobbyId.isNullOrEmpty() || params.members.isEmpty()) {
                LoadingScreen()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(
                            R.string.players_in_lobby,
                            params.members.size
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(params.members) { member ->
                            PlayerRow(
                                playerName = member.user.displayName,
                                isReady = member.isReady
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AuthButton(
                        text = stringResource(R.string.ready),
                        onClick = params.toggleReady
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AuthButton(
                        text = stringResource(R.string.exit),
                        onClick = {
                            params.leaveLobby()
                            navController.popBackStack()
                        }
                    )

                    if (params.isHost && params.allReady) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AuthButton(
                            text = stringResource(R.string.start),
                            onClick = {
                                params.startGame()
                                navController.navigate(CyberopoliRoute.Match)
                            }
                        )
                    }
                }
            }
        }
    }
}