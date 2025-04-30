package com.unibo.cyberopoli.ui.screens.scan.composables

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(
    private val onCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var hasScanned = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val input = InputImage.fromMediaImage(
            mediaImage, imageProxy.imageInfo.rotationDegrees
        )
        scanner.process(input).addOnSuccessListener { barcodes ->
            if (!hasScanned) {
                barcodes.firstOrNull()?.rawValue?.let { code ->
                    onCodeScanned(code)
                    hasScanned = true
                }
            }
        }.addOnFailureListener { Log.e("QRCodeAnalyzer", "Error processing image: $it") }
            .addOnCompleteListener { imageProxy.close() }
    }
}
