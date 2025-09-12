package com.example.news.utils

import androidx.annotation.StringRes
import com.example.news.R

enum class AppTheme(val value: String, @field:StringRes val stringResId: Int) {
    LIGHT("Light", R.string.light_theme),
    DARK("Dark", R.string.dark_theme),
    SYSTEM("System", R.string.system_theme);

    companion object {
        fun String.toAppTheme(): AppTheme {
            return entries.firstOrNull { it.value == this } ?: SYSTEM
        }

        fun Int.toAppTheme(): AppTheme {
            return entries.firstOrNull { it.stringResId == this } ?: SYSTEM
        }
    }
}
