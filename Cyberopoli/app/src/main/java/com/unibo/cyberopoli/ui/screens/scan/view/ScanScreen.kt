package com.unibo.cyberopoli.ui.screens.scan.view

import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
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
import com.unibo.cyberopoli.data.models.auth.AuthState
import com.unibo.cyberopoli.ui.components.BottomBar
import com.unibo.cyberopoli.ui.components.CyberOutlinedTextField
import com.unibo.cyberopoli.ui.components.TopBar
import com.unibo.cyberopoli.ui.navigation.CyberopoliRoute
import com.unibo.cyberopoli.ui.screens.auth.view.composables.Text3D
import com.unibo.cyberopoli.ui.screens.scan.view.composables.QRCodeScanner
import com.unibo.cyberopoli.ui.screens.scan.viewmodel.ScanParams
import com.unibo.cyberopoli.util.PermissionHandler

@Composable
fun ScanScreen(
    navController: NavHostController,
    scanParams: ScanParams,
) {
    val manualCode = remember { mutableStateOf("") }
    val invalidCode = stringResource(R.string.invalid_code)
    val activity = LocalActivity.current as ComponentActivity
    val permissionHandler = remember { PermissionHandler(activity) }
    var hasCameraPermission by remember { mutableStateOf(permissionHandler.hasCameraPermission()) }
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(
                activity,
                activity.getString(R.string.camera_permission_denied),
                Toast.LENGTH_SHORT
            ) // Usa string resource
                .show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) launcher.launch(Manifest.permission.CAMERA)
    }
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            if (scanParams.authState.value === AuthState.Authenticated) {
                BottomBar(navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text3D(text = stringResource(R.string.scan))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (hasCameraPermission) {
                    QRCodeScanner(onQRCodeScanned = { value ->
                        try {
                            scanParams.setScannedValue(value)
                            navController.navigate(CyberopoliRoute.Lobby)
                        } catch (e: Exception) {
                            Toast.makeText(navController.context, invalidCode, Toast.LENGTH_SHORT)
                                .show()
                            return@QRCodeScanner
                        }
                    })
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.camera_permission_required),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { launcher.launch(Manifest.permission.CAMERA) },
                        ) {
                            Text(stringResource(R.string.request_camera))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.qrcode_hint),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(8.dp))

            CyberOutlinedTextField(
                value = manualCode,
                placeholder = stringResource(R.string.enter_code),
                imageVector = Icons.Default.QrCode,
                singleLine = true,
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = manualCode.value.length >= 4,
                onClick = {
                    scanParams.setScannedValue(manualCode.value)
                    navController.navigate(CyberopoliRoute.Lobby)
                },
            ) {
                Text(stringResource(R.string.enter))
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
