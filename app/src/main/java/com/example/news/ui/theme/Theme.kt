package com.example.news.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.news.utils.THEME_DARK
import com.example.news.utils.THEME_LIGHT
import com.example.news.utils.THEME_SYSTEM

private val DarkColorScheme = darkColorScheme(
    background = Black,
    onBackground = White
)

private val LightColorScheme = lightColorScheme(
    background = White,
    onBackground = Black
)

@Composable
fun NewsTheme(
    themePreference: String = THEME_SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themePreference) {
        THEME_LIGHT -> false
        THEME_DARK -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
