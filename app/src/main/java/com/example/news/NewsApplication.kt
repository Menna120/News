package com.example.news

import android.app.Application
import com.example.news.utils.LocaleManagerUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleManagerUtils.applyLocaleOnStartup(this)
    }
}
