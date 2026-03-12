package com.unibo.cyberopoli.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(private val activity: ComponentActivity) {
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.CAMERA,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE,
            )
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}
