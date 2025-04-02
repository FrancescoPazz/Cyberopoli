package com.example.cyberopoli.ui.screens.scan

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.CyberopoliRoute
import com.example.cyberopoli.ui.composables.BottomBar
import com.example.cyberopoli.ui.composables.TopBar
import com.example.cyberopoli.ui.composables.auth.Text3D
import com.example.cyberopoli.ui.composables.scan.QRCodeScanner
import com.example.cyberopoli.ui.screens.auth.AuthState
import com.example.cyberopoli.ui.screens.auth.AuthViewModel

@Composable
fun ScanScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val appName = stringResource(R.string.app_name).lowercase()
    val invalidCode = stringResource(R.string.invalid_code)
    var scannedValue by remember { mutableStateOf("") }

    val authState by authViewModel.authState.observeAsState()

    Scaffold(topBar = { TopBar(navController) }, bottomBar = {
        if (authState == AuthState.Authenticated) BottomBar(navController)
    }, content = { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text3D(
                    stringResource(R.string.scan),
                    textColor = MaterialTheme.colorScheme.tertiary,
                    shadowColor = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(46.dp))

                QRCodeScanner { value ->
                    scannedValue = value
                    if (scannedValue.contains(appName)) {
                        navController.navigate(CyberopoliRoute.ARScreen)
                    } else {
                        Toast.makeText(navController.context, invalidCode, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                Spacer(modifier = Modifier.height(46.dp))

                Text(
                    text = stringResource(R.string.qrcode_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    })

}
