package com.example.news.ui.screens

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullArticleScreen(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }
    var isLoading by remember { mutableStateOf(true) }

    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    webView.apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageCommitVisible(view: WebView?, url: String?) {
                                super.onPageCommitVisible(view, url)
                                isLoading = false
                            }
                        }
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            BackHandler(enabled = webView.canGoBack()) {
                webView.goBack()
            }
        }
    }
}
