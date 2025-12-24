package com.example.qrscan.database

import androidx.room.TypeConverter
import com.example.qrscan.database.data.QRType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    private val mapType = object : TypeToken<Map<String,String>>() {}.type

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String =
        gson.toJson(map)

    @TypeConverter
    fun toMap(json: String): Map<String, String> =
        gson.fromJson(json, mapType)

    @TypeConverter
    fun fromQrType(type: QRType): String = type.name

    @TypeConverter
    fun toQrType(value: String): QRType = QRType.valueOf(value)



}