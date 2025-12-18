package com.example.qrscan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrscan.database.DatabaseProvider
import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRType
import com.example.qrscan.model.ScanModel
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val model: ScanModel


    private val _createOption = MutableStateFlow<String?>(null)
    var userInput: Map<String, String> = emptyMap()

    var itemIdCreate : Int=0


    var byteQR: ByteArray? = null

    var content : String?=null
    var createOption = _createOption.asStateFlow()

    init {
        val context = application.applicationContext
        val db = DatabaseProvider.getDatabase(context)
        val dao = db.qrItemDao()
        model = ScanModel(dao)
    }

    fun setCreateOption(value: String?) {
        _createOption.value = value
    }

    fun saveCustomQR(type: QRType, content: String?, data: Map<String, String>) {
        viewModelScope.launch {
            if (content == null || byteQR == null) return@launch
            model.saveCustomQR(type, content, data, byteQR!!)
        }
    }
    fun getAll(): Flow<List<QRCodeEntity>> {
        return model.getAll()
    }
   suspend fun deleteById(id : Int){
        model.deleteById(id)
    }
    suspend fun getById(id : Int) : QRCodeEntity?{
         return model.getBitmapfromId(id)
    }
    fun insertQRCode(qr: QRCodeEntity) = viewModelScope.launch {
        model.insert(qr)
    }

    fun updateQRCode(qr: QRCodeEntity) = viewModelScope.launch {
        model.update(qr)
    }



}
