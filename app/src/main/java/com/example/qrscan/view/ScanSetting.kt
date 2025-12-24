package com.example.qrscan.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object ScanSetting {

    fun play(context: Context, beep: Boolean, vibrate: Boolean) {
        if (beep) playBeep()
        if (vibrate) playVibrate(context)
    }

    private fun playBeep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
    }

    private fun playVibrate(context: Context) {
        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    120,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(120)
        }
    }
}
