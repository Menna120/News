package com.example.news.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object OpenUrlInExternalBrowser {
    fun openBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}
