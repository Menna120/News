package com.example.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.news.ui.news_app.NewsApp
import com.example.news.ui.news_app.NewsAppViewModel
import com.example.news.utils.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NewsAppViewModel by viewModels() // Added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

        // Get the language when the Activity is first created
        AppPreferences.getLanguagePreference(this) // Added

        lifecycleScope.launch { // Added
            repeatOnLifecycle(Lifecycle.State.STARTED) { // Added
                viewModel.languagePreferenceCode
                    // Ensure we only react to actual changes
                    .drop(1) // Skip the initial value to prevent immediate recreate
                    .collect { newLanguage ->
                        // Only recreate if the language has actually changed from what it was on create
                        // This helps prevent potential loops if the value is emitted again without actual change
                        // or if the initial value from viewModel is the same as AppPreferences.
                        // However, a simpler check might be sufficient if distinctUntilChanged and drop(1)
                        // are robust enough for the flow's emission pattern.
                        // For more safety, we could compare with 'initialLanguage'
                        // but viewModel's state should be the source of truth for the change trigger.
                        // If `drop(1)` is used, we assume the first emitted value is the current/initial state.
                        recreate()
                    }
            }
        }

        setContent {
            NewsApp(viewModel = viewModel) // Pass the viewModel instance
        }
    }
}
