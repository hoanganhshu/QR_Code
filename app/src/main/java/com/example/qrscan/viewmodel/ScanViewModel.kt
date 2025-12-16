package com.example.qrscan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrscan.database.DatabaseProvider
import com.example.qrscan.model.ScanModel
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val model: ScanModel

    // public read-only StateFlow, private mutable
    private val _createOption = MutableStateFlow<String?>(null)

     lateinit var byteQR : ByteArray
    val createOption = _createOption.asStateFlow()

    init {
        val context = application.applicationContext
        val db = DatabaseProvider.getDatabase(context)
        val dao = db.qrItemDao()
        model = ScanModel(dao)
    }

    fun setCreateOption(value: String?) {
        _createOption.value = value
    }

    fun onBarcodeScanned(barcode: Barcode) {
        viewModelScope.launch {
            model.saveBarcode(barcode)
        }
    }
}
