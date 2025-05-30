package com.unibo.cyberopoli.ui.screens.scan.composables

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRCodeScanner(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView =
        remember {
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        }

    DisposableEffect(previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            val cameraProvider =
                try {
                    cameraProviderFuture.get()
                } catch (e: Exception) {
                    Log.e("QRCodeScanner", "Error getting camera provider", e)
                    throw e
                }

            val previewUseCase =
                Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

            val analysisUseCase =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                    .also { analysis ->
                        analysis.setAnalyzer(
                            executor,
                            QRCodeAnalyzer { code ->
                                onQRCodeScanned(code)
                            },
                        )
                    }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    previewUseCase,
                    analysisUseCase,
                )
            } catch (e: Exception) {
                Log.e("QRCodeScanner", "Error bind camera", e)
                throw e
            }
        }, executor)

        onDispose {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier =
            Modifier
                .size(300.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .padding(4.dp),
    )
}
