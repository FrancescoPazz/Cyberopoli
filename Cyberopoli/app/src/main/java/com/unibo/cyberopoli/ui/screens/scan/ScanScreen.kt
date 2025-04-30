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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

    var hasCameraPermission by remember { mutableStateOf(permissionHandler.hasCameraPermission()) }
    var manualCode by remember { mutableStateOf("") }
    val appName = stringResource(R.string.app_name).lowercase()
    val invalidCode = stringResource(R.string.invalid_code)

    val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        hasCameraPermission = granted
        if (!granted) Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT)
            .show()
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(topBar = { TopBar(navController) }, bottomBar = {
        if (scanParams.authState.value === AuthState.Authenticated) BottomBar(navController)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text3D(
                    text = stringResource(R.string.scan),
                    textColor = MaterialTheme.colorScheme.tertiary,
                    shadowColor = MaterialTheme.colorScheme.secondary
                )
            }

            if (hasCameraPermission) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    QRCodeScanner { value ->
                        if (value.contains(appName)) {
                            scanParams.setScannedValue(value)
                            navController.navigate(CyberopoliRoute.Lobby)
                        } else {
                            Toast.makeText(navController.context, invalidCode, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.camera_permission_required),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                            Text("stringResource(R.string.request_camera)")
                        }
                        Spacer(Modifier.height(32.dp))
                        OutlinedTextField(
                            value = manualCode,
                            onValueChange = { manualCode = it.filter { ch -> ch.isDigit() } },
                            label = { Text("stringResource(R.string.enter_code)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(enabled = manualCode.length >= 4, onClick = {
                            scanParams.setScannedValue(manualCode)
                            navController.navigate(CyberopoliRoute.Lobby)
                        }) {
                            Text(stringResource(R.string.enter))
                        }
                    }
                }
            }
        }
    }
}
