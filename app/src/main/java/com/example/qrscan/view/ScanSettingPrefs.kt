package com.example.qrscan.view

import android.content.Context

object ScanSettingPrefs {

    private const val PREF_NAME = "scan_setting"
    private const val KEY_SAVE_HISTORY = "save_history"
    private const val KEY_BEEP = "beep"
    private const val KEY_VIBRATE = "vibrate"
    private const val KEY_AUTO_SCAN = "auto_scan"

    fun saveBoolean(context: Context, key: String, value: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getBoolean(context: Context, key: String, def: Boolean): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(key, def)
    }
}
