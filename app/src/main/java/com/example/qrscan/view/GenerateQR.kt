package com.example.qrscan.view


import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object GenerateQR {
    fun generateQR(content : String ,size : Int =1024,margin : Int = 1,errorCorrection : ErrorCorrectionLevel = ErrorCorrectionLevel.M) : Bitmap{

        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to margin,
            EncodeHintType.ERROR_CORRECTION to errorCorrection
        )
        val matrix  = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,size,size,hints)
        return toBitmap(matrix)





    }
    private fun toBitmap(matrix: BitMatrix): Bitmap {
        val w = matrix.width
        val h = matrix.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE } }
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            .apply { setPixels(pixels, 0, w, 0, 0, w, h) } }
}