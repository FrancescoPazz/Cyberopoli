package com.unibo.cyberopoli.ui.contracts

data class ScanParams(
    val setScannedValue: (String) -> Unit,
    val authState: AuthState
)