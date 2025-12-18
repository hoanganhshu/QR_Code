package com.example.qrscan.model

import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRType
import QrItemDao
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.Flow

class ScanModel(
    private val qrCodeDao: QrItemDao
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

    fun getAll(): Flow<List<QRCodeEntity>> {
        return qrCodeDao.getAll()
    }
     suspend fun deleteById(id : Int) {
        qrCodeDao.deleteById(id)

    }
    suspend fun getBitmapfromId(id : Int) : QRCodeEntity?{
        return qrCodeDao.getById(id)
    }



}
