package com.example.news.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private const val PREFERENCES_FILE_KEY = "com.example.news.preferences"
    private const val KEY_THEME_PREFERENCE = "theme_preference"
    private const val KEY_LANGUAGE_PREFERENCE = "language_preference"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    fun getThemePreference(context: Context): String {
        return getSharedPreferences(context)
            .getString(KEY_THEME_PREFERENCE, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    fun saveThemePreference(context: Context, themePreference: String) {
        getSharedPreferences(context).edit {
            putString(KEY_THEME_PREFERENCE, themePreference)
        }
    }

    fun getLanguagePreference(context: Context): String {
        return getSharedPreferences(context)
            .getString(KEY_LANGUAGE_PREFERENCE, LANG_CODE_ENGLISH) ?: LANG_CODE_ENGLISH
    }

    fun saveLanguagePreference(context: Context, languageCode: String) {
        getSharedPreferences(context).edit {
            putString(KEY_LANGUAGE_PREFERENCE, languageCode)
        }
    }
}
