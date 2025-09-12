package com.example.news.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @get:ApplicationContext private val application: Application
) {
    private val preferencesFileKey = "com.example.news.preferences"
    private val themePreferencesKey = "theme_preference"

    private val getSharedPreferences: SharedPreferences =
        application.getSharedPreferences(preferencesFileKey, Context.MODE_PRIVATE)

    fun getThemePreference(): String {
        return getSharedPreferences
            .getString(themePreferencesKey, AppTheme.SYSTEM.value) ?: AppTheme.SYSTEM.value
    }

    fun saveThemePreference(themePreference: String) {
        getSharedPreferences.edit {
            putString(themePreferencesKey, themePreference)
        }
    }
}
