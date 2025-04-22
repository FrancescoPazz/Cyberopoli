package com.unibo.cyberopoli.ui.composables.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.CyberopoliRoute

@Composable
fun GuestCard(
    navController: NavController, signInAnonymously: (String, (Boolean, String?) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val guestName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthOutlinedTextField(
            value = guestName,
            placeholder = stringResource(R.string.name),
            imageVector = Icons.Default.Person,
            singleLine = true,
        )

        AuthButton(
            text = stringResource(R.string.enter).uppercase(), onClick = {
                if (guestName.value.isNotBlank()) {
                    signInAnonymously(guestName.value) { success, _ ->
                        if (!success) {
                            Toast.makeText(context, R.string.enter_guest_error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    navController.navigate(CyberopoliRoute.Scan) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
