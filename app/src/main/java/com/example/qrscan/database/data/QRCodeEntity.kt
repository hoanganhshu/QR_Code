package com.example.qrscan.database.data

import android.R.attr.name
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.qrscan.database.Converters

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
@TypeConverters(Converters::class)
data class QRCodeEntity(
    @PrimaryKey(autoGenerate = true) val id : Int=0,
    @ColumnInfo(name ="type")  val type : QRType,
    @ColumnInfo(name="content") val content: String,
    @ColumnInfo(name = "createdAt") val createdAt : Long,
    @ColumnInfo(name = "data")  val data: Map<String, String>,
    @ColumnInfo(name="image") val image: ByteArray?





)
