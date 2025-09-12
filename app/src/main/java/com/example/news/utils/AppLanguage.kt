package com.example.news.utils

import androidx.annotation.StringRes
import com.example.news.R

enum class AppLanguage(val code: String, @field:StringRes val stringResId: Int) {
    ENGLISH("en", R.string.en),
    ARABIC("ar", R.string.ar);

    companion object {
        fun String.toAppLanguage(): AppLanguage {
            return entries.firstOrNull { it.code == this } ?: ENGLISH
        }

        fun Int.toAppLanguage(): AppLanguage {
            return entries.firstOrNull { it.stringResId == this } ?: ENGLISH
        }
    }
}
