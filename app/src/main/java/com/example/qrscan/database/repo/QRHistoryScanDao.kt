package com.example.qrscan.database.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qrscan.database.data.QRCodeHistoryScanEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface QRHistoryScanDao {

    @Query("SELECT * FROM QRCodeHistoryScan ORDER BY createdAt DESC")
    fun getAll(): Flow<List<QRCodeHistoryScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: QRCodeHistoryScanEntity)

    @Update
    suspend fun update(item: QRCodeHistoryScanEntity)

    @Query("DELETE FROM QRCodeHistoryScan WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM QRCodeHistoryScan")
    suspend fun deleteAll()

    @Query("SELECT * FROM QRCodeHistoryScan WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): QRCodeHistoryScanEntity?

    @Query("DELETE FROM QRCodeHistoryScan WHERE id IN (:ids)")
    suspend fun deleteListById(ids: List<Int>)






}