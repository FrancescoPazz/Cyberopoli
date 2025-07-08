package com.unibo.cyberopoli.ui.screens.lobby.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.lobby.LobbyMember

@Composable
fun LobbyContent(
    members: List<LobbyMember>,
    isHost: State<Boolean?>,
    isReady: Boolean,
    allReady: State<Boolean?>,
    onToggleReadyClick: () -> Unit,
    onStartGameClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uniquedMembers = members.distinctBy { it.userId }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.players_in_lobby, members.size),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        LazyColumn(modifier = modifier.weight(1f)) {
            items(uniquedMembers, key = { it.userId }) { member ->
                PlayerRow(
                    playerName = member.user?.username ?: member.userId,
                    isReady = member.isReady,
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LobbyActions(
            isHost = isHost,
            allReady = allReady,
            onToggleReadyClick = onToggleReadyClick,
            onStartGameClick = onStartGameClick,
            onExitClick = onExitClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            isReady = isReady,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
