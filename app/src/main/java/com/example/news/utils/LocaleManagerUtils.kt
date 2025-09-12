package com.example.news.utils

import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManagerUtils {

    private fun setAppLocale(context: Context, languageCode: String) {
        val localeListCompat =
            if (languageCode.isNotBlank())
                LocaleListCompat.forLanguageTags(languageCode)
            else LocaleListCompat.getEmptyLocaleList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager =
                context.applicationContext.getSystemService(LocaleManager::class.java)
            localeManager?.applicationLocales =
                LocaleList.forLanguageTags(languageCode.ifBlank { null })
        }
        AppCompatDelegate.setApplicationLocales(localeListCompat)
    }

    fun applyLocaleOnStartup(application: Application) {
        setAppLocale(application, AppPreferences.getLanguagePreference(application))
    }
}
