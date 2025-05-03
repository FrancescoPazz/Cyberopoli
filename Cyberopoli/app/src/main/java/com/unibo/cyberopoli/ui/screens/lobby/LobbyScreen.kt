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
import androidx.compose.ui.Alignment
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
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.auth.composables.AuthButton
import com.unibo.cyberopoli.ui.screens.loading.LoadingScreen
import com.unibo.cyberopoli.ui.screens.lobby.composables.PlayerRow

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun LobbyScreen(
    navController: NavHostController, params: LobbyParams
) {
    var hasJoined by remember { mutableStateOf(false) }
    var suppressLeaveOnStop by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && !suppressLeaveOnStop) {
                Log.d("LobbyScreen", "ON_STOP: leaving lobby")
                params.leaveLobby()
                navController.popBackStack()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(params.lobbyId) {
        if (!hasJoined && params.lobbyId.isNotBlank()) {
            params.startLobbyFlow(params.lobbyId)
            hasJoined = true
        }
    }

    BackHandler {
        navController.popBackStack()
    }

    Scaffold(topBar = {
        TopBar(navController) {
            navController.popBackStack()
        }
    }, bottomBar = { if (!params.isGuest) BottomBar(navController) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (params.lobbyId.isEmpty() || params.members.isEmpty()) {
                LoadingScreen()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(
                            R.string.players_in_lobby, params.members.size
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(params.members) { member ->
                            Log.d("LobbyScreen", "Member: $member")
                            PlayerRow(
                                playerName = member.user?.username ?: member.userId,
                                isReady = member.isReady,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AuthButton(
                        text = stringResource(R.string.ready), onClick = params.toggleReady
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AuthButton(text = stringResource(R.string.exit), onClick = {
                        navController.popBackStack()
                    })

                    if (params.isHost && params.allReady) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AuthButton(text = stringResource(R.string.start), onClick = {
                            Log.d("LobbyScreen", "Starting game...")
                            suppressLeaveOnStop = true
                            params.startGame()
                            navController.navigate(CyberopoliRoute.Game) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                    }
                }
            }
        }
    }
}