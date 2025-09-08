package com.example.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.news.ui.drawer.MainAppContent
import com.example.news.utils.AppPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

        val initialTheme = AppPreferences.getThemePreference(this)
        val initialLanguageCode = AppPreferences.getLanguagePreference(this)

        setContent {
            MainAppContent(
                initialTheme = initialTheme,
                initialLanguageCode = initialLanguageCode
            )
        }
    }
}
