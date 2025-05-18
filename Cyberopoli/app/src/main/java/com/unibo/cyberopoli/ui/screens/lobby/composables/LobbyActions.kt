package com.unibo.cyberopoli.ui.screens.lobby.composables

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
import com.unibo.cyberopoli.ui.screens.auth.composables.AuthButton

@Composable
fun LobbyActions(
    isHost: State<Boolean?>,
    allReady: State<Boolean?>,
    onToggleReadyClick: () -> Unit,
    onStartGameClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthButton(
            text = stringResource(R.string.ready),
            onClick = onToggleReadyClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        AuthButton(
            text = stringResource(R.string.exit),
            onClick = onExitClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        if (isHost.value == true && allReady.value == true) {
            Spacer(modifier = Modifier.height(16.dp))
            AuthButton(
                text = stringResource(R.string.start),
                onClick = onStartGameClick,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}
