package com.example.cyberopoli.ui.composables.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.CyberopoliRoute

@Composable
fun GuestCard(
    navController: NavController,
) {
    var guestName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthOutlinedTextField(
            value = guestName,
            placeholder = stringResource(R.string.name),
            imageVector = Icons.Default.PermIdentity,
            singleLine = true,
        )

        AuthButton(
            text = stringResource(R.string.enter).uppercase(), onClick = {
                if (guestName.value.isNotEmpty()) navController.navigate(CyberopoliRoute.Scan) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
