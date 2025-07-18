package com.unibo.cyberopoli.ui.screens.lobby.view.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.screens.auth.view.composables.AuthButton

@Composable
fun LobbyActions(
    user: User,
    isHost: (user: User) -> Boolean,
    allReady: State<Boolean?>,
    isReady: Boolean,
    onToggleReadyClick: (user: User) -> Unit,
    onStartGameClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthButton(
            text = stringResource(if (isReady) R.string.not_ready else R.string.ready),
            onClick = { onToggleReadyClick(user) },
            modifier = Modifier.fillMaxWidth(0.8f),
        )
        Spacer(modifier = Modifier.height(8.dp))
        AuthButton(
            text = stringResource(R.string.exit),
            onClick = onExitClick,
            modifier = Modifier.fillMaxWidth(0.8f),
        )
        if (isHost(user) && allReady.value == true) {
            Spacer(modifier = Modifier.height(16.dp))
            AuthButton(
                text = stringResource(R.string.start),
                onClick = onStartGameClick,
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }
    }
}
