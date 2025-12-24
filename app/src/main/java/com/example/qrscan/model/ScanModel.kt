package com.example.qrscan.model

import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRType
import QrItemDao
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import com.example.qrscan.database.data.QRCodeHistoryScanEntity
import com.example.qrscan.database.repo.QRHistoryScanDao
import com.example.qrscan.view.ParsedQR
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.Flow

class ScanModel(
    private val qrCodeDao: QrItemDao,
    private val qrHistoryScanDao: QRHistoryScanDao
) {

    suspend fun saveCustomQR(
        id: Int,
        type: QRType,
        content: String,
        data: Map<String, String>,
        image: ByteArray,
        createdAt: Long? = null
    ) {
        val entity = QRCodeEntity(
            id = id,
            type = type,
            content = content,
            createdAt = createdAt ?: System.currentTimeMillis(),
            data = data,
            image = image
        )

        if (id == 0) {
            qrCodeDao.insert(entity)
        } else {
            qrCodeDao.update(entity)
        }
    }
    suspend fun saveHistoryScanQR(
        id: Int,
        type: QRType,
        content: String,
        data: Map<String,String>,
        image: ByteArray,
        createdAt: Long? = null
    ) {
        val entity = QRCodeHistoryScanEntity(
            id = id,
            type = type,
            content = content,
            createdAt = createdAt ?: System.currentTimeMillis(),
            data = data,
            image = image
        )


            qrHistoryScanDao.insert(entity)

    }



    fun getAll(): Flow<List<QRCodeEntity>> {
        return qrCodeDao.getAll()
    }
    suspend fun deleteListById(ids : List<Int>){
        qrHistoryScanDao.deleteListById(ids)
    }
    suspend fun deleteCustomQRListById(ids : List<Int>){
        qrCodeDao.deleteListById(ids)
    }
    fun getAllHistoryScan(): Flow<List<QRCodeHistoryScanEntity>> {
        return qrHistoryScanDao.getAll()
    }
     suspend fun deleteById(id : Int) {
        qrCodeDao.deleteById(id)

    }
    suspend fun getBitmapfromId(id : Int) : QRCodeEntity?{
        return qrCodeDao.getById(id)
    }
    suspend fun deleteScanById(id : Int) {
        qrHistoryScanDao.deleteById(id)

    }
    suspend fun getBitmapScanfromId(id : Int) : QRCodeHistoryScanEntity?{
        return qrHistoryScanDao.getById(id)
    }




}
