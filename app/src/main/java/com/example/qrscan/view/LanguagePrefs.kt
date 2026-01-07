package com.example.qrscan.view

import android.content.Context

object LanguagePrefs {

    private const val PREF_NAME = "app_language"
    private const val KEY_LANGUAGE = "language"

    fun save(context: Context, language: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun get(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "English") ?: "English"
    }
}
