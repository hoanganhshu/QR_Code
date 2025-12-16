package com.example.qrscan.database

import QrItemDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.qrscan.database.data.QRCodeEntity
import kotlin.jvm.java

@Database(entities = [QRCodeEntity :: class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseQRCode : RoomDatabase(){
    abstract fun qrItemDao() : QrItemDao
}

object DatabaseProvider {
    fun getDatabase(context: Context): DatabaseQRCode {
        return Room.databaseBuilder(
            context.applicationContext,
            DatabaseQRCode::class.java,
            "qr_items_db"
        ).build()
    }
}
