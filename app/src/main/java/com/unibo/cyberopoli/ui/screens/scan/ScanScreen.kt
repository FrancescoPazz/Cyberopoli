package com.unibo.cyberopoli.ui.screens.scan

import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.auth.AuthState
import com.unibo.cyberopoli.ui.screens.auth.composables.Text3D
import com.unibo.cyberopoli.ui.screens.scan.composables.QRCodeScanner
import com.unibo.cyberopoli.util.PermissionHandler

@Composable
fun ScanScreen(
    navController: NavHostController, scanParams: ScanParams
) {
    val activity = LocalActivity.current as ComponentActivity
    val permissionHandler = remember { PermissionHandler(activity) }

    val appName = stringResource(R.string.app_name).lowercase()
    val invalidCode = stringResource(R.string.invalid_code)
    var scannedValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!permissionHandler.hasCameraPermission()) {
            permissionHandler.requestCameraPermission()
        }
    }

    Scaffold(topBar = { TopBar(navController) }, bottomBar = {
        if (scanParams.authState.value === AuthState.Authenticated) BottomBar(navController)
    }, content = { paddingValues ->

        if (!permissionHandler.hasCameraPermission()) {
            val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
                if (granted) {
                    permissionHandler.requestCameraPermission()
                } else {
                    Toast.makeText(
                        activity, "Camera permission denied", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center
            ) {
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Request Camera Permission")
                }
            }
        } else {
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
                            scanParams.setScannedValue(scannedValue)
                            navController.navigate(CyberopoliRoute.Lobby)
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
        }
    })

}
