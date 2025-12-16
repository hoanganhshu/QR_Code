package com.example.qrscan.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class QRType{
    PHONE,
    EMAIL,
    TEXT,
    URL,
    SMS,
    CONTACT,
    LOCATION,
    PRODUCT,
    EVENT,
    WIFI,
    ISBN


}



@Entity(tableName = "QRCode")
data class QRCodeEntity(
    @PrimaryKey(autoGenerate = true) val id : Long=0,

    @ColumnInfo(name ="type")  val type : QRType,
    @ColumnInfo(name = "createdAt") val createdAt : Long,
    @ColumnInfo(name = "data")  val data: Map<String, Any?>,




)
