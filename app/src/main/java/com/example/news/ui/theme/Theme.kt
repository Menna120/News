package com.example.news.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.news.utils.AppTheme

private val DarkColorScheme = darkColorScheme(
    background = Black,
    onBackground = White,
    outline = Gray
)

private val LightColorScheme = lightColorScheme(
    background = White,
    onBackground = Black,
    outline = Gray
)

@Composable
fun NewsTheme(
    theme: String = AppTheme.SYSTEM.value,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (theme) {
        AppTheme.LIGHT.value -> false
        AppTheme.DARK.value -> true
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
