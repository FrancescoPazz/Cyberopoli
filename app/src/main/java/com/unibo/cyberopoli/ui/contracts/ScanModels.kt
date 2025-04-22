package com.unibo.cyberopoli.ui.contracts

import androidx.lifecycle.LiveData

data class ScanParams(
    val setScannedValue: (String) -> Unit, val authState: LiveData<AuthState>
)