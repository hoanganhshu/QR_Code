package com.example.qrscan.database



import QrItemDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.qrscan.database.data.QRCodeEntity
import com.example.qrscan.database.data.QRCodeHistoryScanEntity
import com.example.qrscan.database.repo.QRHistoryScanDao
import kotlin.jvm.java

@Database(entities = [QRCodeHistoryScanEntity :: class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseQRCodeHistoryScan : RoomDatabase(){
    abstract fun qrItemHistoryScanDao() : QRHistoryScanDao
}

object DatabaseHistoryScanProvider {
    fun getDatabase(context: Context): DatabaseQRCodeHistoryScan {
        return Room.databaseBuilder(
            context.applicationContext,
            DatabaseQRCodeHistoryScan::class.java,
            // History DB
            "qr_history.db"

        ).fallbackToDestructiveMigration().build()
    }
}
