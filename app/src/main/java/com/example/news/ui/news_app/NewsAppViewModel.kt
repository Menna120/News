package com.example.news.ui.news_app

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
    private val _themePreference: MutableStateFlow<String> =
        MutableStateFlow(appPreferences.getThemePreference())
    val themePreference: StateFlow<String> = _themePreference.asStateFlow()

    private val _languagePreferenceCode: MutableStateFlow<String> =
        MutableStateFlow(AppCompatDelegate.getApplicationLocales()[0]?.displayName ?: "")
    val languagePreferenceCode: StateFlow<String> = _languagePreferenceCode.asStateFlow()

    private val _appBarSearchQuery = MutableStateFlow("")
    val appBarSearchQuery: StateFlow<String> = _appBarSearchQuery.asStateFlow()

    fun updateThemePreference(newPreference: String) {
        viewModelScope.launch {
            _themePreference.value = newPreference
            appPreferences.saveThemePreference(newPreference)
        }
    }

    fun updateLanguagePreferenceCode(newCode: String) {
        localeManagerUtils.setAppLocale(newCode)
        _languagePreferenceCode.value = newCode
    }

    fun updateAppBarSearchQuery(query: String) {
        _appBarSearchQuery.value = query
    }
}
