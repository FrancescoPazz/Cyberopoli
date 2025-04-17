package com.unibo.cyberopoli.ui.composables.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.unibo.cyberopoli.R

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
