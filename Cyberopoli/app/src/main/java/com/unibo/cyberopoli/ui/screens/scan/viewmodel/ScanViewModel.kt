package com.unibo.cyberopoli.ui.screens.scan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanViewModel : ViewModel() {
    val scannedValue = MutableLiveData<String>()

    fun setScannedValue(value: String) {
        scannedValue.value = value
    }
}
