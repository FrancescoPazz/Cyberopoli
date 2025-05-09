package com.unibo.cyberopoli.ui.screens.settings.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.unibo.cyberopoli.R

@Composable
fun LogoutButton(
    onLogout: () -> Unit
) {
    Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Text(stringResource(R.string.logout))
    }
}
