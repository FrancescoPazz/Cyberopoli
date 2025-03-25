package com.example.cyberopoli.ui.screens.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.cyberopoli.ui.composables.scan.QRCodeScanner
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.composables.AppBar
import com.example.cyberopoli.ui.composables.auth.Text3D

@Composable
fun ScanScreen(navController: NavHostController) {
    var scannedValue by remember { mutableStateOf("") }

    Scaffold (
        topBar = { AppBar(navController) },
        content = { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text3D(stringResource(R.string.scan), modifier = Modifier.padding(bottom = 20.dp))
                    QRCodeScanner { value ->
                        scannedValue = value
                    }
                }
            }
        }
    )

}
