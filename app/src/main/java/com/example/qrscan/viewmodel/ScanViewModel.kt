package com.example.qrscan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrscan.database.DatabaseHistoryScanProvider
import com.example.qrscan.database.DatabaseProvider
import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRCodeHistoryScanEntity
import com.example.qrscan.database.data.QRType
import com.example.qrscan.model.ScanModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val model: ScanModel


    private val _createOption = MutableStateFlow<QRType?>(null)
    var userInput: Map<String, String> = emptyMap()

    private val _scannedType = MutableStateFlow<QRType?>(null)
    val scannedType = _scannedType


    private val _userScan = MutableStateFlow<Map<String, String>>(emptyMap())
    val userScan = _userScan.asStateFlow()


    var itemIdCreate : Int=0

    var isSave : Boolean = false
    private val _autoScanEnabled = MutableStateFlow(true)

    private val _scannedRaw = MutableStateFlow<String?>(null)
    var scannedRaw = _scannedRaw.asStateFlow()

    var byteQR: ByteArray? = null
    var byteScanQR: ByteArray? = null

    var content : String?=null
    var createOption = _createOption.asStateFlow()

    init {
        val context = application.applicationContext
        val db = DatabaseProvider.getDatabase(context)
        val dao = db.qrItemDao()
        val database = DatabaseHistoryScanProvider.getDatabase(context)
        val daoHistory=database.qrItemHistoryScanDao()
        model = ScanModel(dao,daoHistory)
    }
    val autoScanEnabled = _autoScanEnabled.asStateFlow()

    fun setAutoScanEnabled(enabled: Boolean) {
        _autoScanEnabled.value = enabled
    }
    fun setUserScan(data: Map<String, String>) {
        _userScan.value = data
    }

    private val _beepEnabled = MutableStateFlow(true)
    val beepEnabled = _beepEnabled.asStateFlow()

    private val _vibrateEnabled = MutableStateFlow(true)
    val vibrateEnabled = _vibrateEnabled.asStateFlow()

    fun setBeepEnabled(enabled: Boolean) {
        _beepEnabled.value = enabled
    }

    fun setVibrateEnabled(enabled: Boolean) {
        _vibrateEnabled.value = enabled
    }
    fun clearCreateOption() {
        _createOption.value = null
    }

    fun setCreateOption(type: QRType) {
        _createOption.value = type
    }

    fun setScanOption(type: QRType) {
        _scannedType.value = type
    }
    fun saveScanned(id : Int,type: QRType, content: String?, data: Map<String, String>) {
        viewModelScope.launch {
            if (content == null || byteScanQR == null) return@launch
            model.saveHistoryScanQR(id,type, content ,data,byteScanQR!!)
        }
    }
    fun getAllScan() : Flow<List<QRCodeHistoryScanEntity>>{
      return  model.getAllHistoryScan()

    }

    fun saveCustomQR(id : Int,type: QRType, content: String?, data: Map<String, String>) {
        viewModelScope.launch {
            if (content == null || byteQR == null) return@launch
            model.saveCustomQR(id,type, content, data, byteQR!!)
        }
    }

    suspend fun deleteListByid(ids : List<Int>){
        model.deleteListById(ids)
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
    suspend fun deleteScanById(id : Int){
        model.deleteScanById(id)
    }
    suspend fun getScanById(id : Int) : QRCodeHistoryScanEntity?{
        return model.getBitmapScanfromId(id)
    }
    suspend fun deleteCustomQRListByid(ids : List<Int>){
        model.deleteCustomQRListById(ids)
    }

}
