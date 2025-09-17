package com.example.news.ui.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.utils.AppPreferences
import com.example.news.utils.LocaleManagerUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsAppViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localeManagerUtils: LocaleManagerUtils
) : ViewModel() {
    private val _theme: MutableStateFlow<String> =
        MutableStateFlow(appPreferences.getThemePreference())
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _languageCode: MutableStateFlow<String> =
        MutableStateFlow(AppCompatDelegate.getApplicationLocales()[0]?.displayName ?: "")
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    private val _appBarSearchQuery = MutableStateFlow("")
    val appBarSearchQuery: StateFlow<String> = _appBarSearchQuery.asStateFlow()

    fun updateTheme(newPreference: String) {
        viewModelScope.launch {
            _theme.value = newPreference
            appPreferences.saveThemePreference(newPreference)
        }
    }

    fun updateLanguageCode(newCode: String) {
        localeManagerUtils.setAppLocale(newCode)
        _languageCode.value = newCode
    }

    fun updateAppBarSearchQuery(query: String) {
        _appBarSearchQuery.value = query
    }
}
