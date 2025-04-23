package com.unibo.cyberopoli.ui.screens.scan

import androidx.lifecycle.LiveData
import com.unibo.cyberopoli.ui.screens.auth.AuthState

data class ScanParams(
    val setScannedValue: (String) -> Unit, val authState: LiveData<AuthState>
)