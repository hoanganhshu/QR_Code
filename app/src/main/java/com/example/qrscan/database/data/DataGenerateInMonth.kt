package com.example.qrscan.database.data

data class DataGenerateInMonth(
    val image : Int,
    val title : String ,
    val subtitle : String,
    val id : Int
)
data class HistoryScan(
    val createAt : Long ,
    val item : List<QRCodeHistoryScanEntity>
)
