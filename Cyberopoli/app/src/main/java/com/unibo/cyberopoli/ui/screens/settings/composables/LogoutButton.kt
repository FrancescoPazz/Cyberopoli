package com.unibo.cyberopoli.ui.screens.settings.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R

@Composable
fun LogoutButton(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onLogout,
        modifier = modifier.padding(horizontal = 4.dp),
        elevation =
            ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp,
            ),
        colors =
            ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error,
            ),
    ) {
        Text(stringResource(R.string.logout))
    }
}
