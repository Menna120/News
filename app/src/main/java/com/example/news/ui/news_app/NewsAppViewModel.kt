package com.example.news.ui.news_app

import android.app.Application
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.utils.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsAppViewModel @Inject constructor(
    @get:ApplicationContext private val application: Application
) : ViewModel() {

    private val _themePreference: MutableStateFlow<String> =
        MutableStateFlow(AppPreferences.getThemePreference(application))
    val themePreference: StateFlow<String> = _themePreference.asStateFlow()

    private val _languagePreferenceCode: MutableStateFlow<String> =
        MutableStateFlow(AppPreferences.getLanguagePreference(application))
    val languagePreferenceCode: StateFlow<String> = _languagePreferenceCode.asStateFlow()

    private val _appBarSearchQuery = MutableStateFlow("")
    val appBarSearchQuery: StateFlow<String> = _appBarSearchQuery.asStateFlow()

    fun updateThemePreference(newPreference: String) {
        viewModelScope.launch {
            _themePreference.value = newPreference
            AppPreferences.saveThemePreference(application, newPreference)
        }
    }

    fun updateLanguagePreferenceCode(newCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager =
                application.getSystemService(LocaleManager::class.java)
            localeManager?.applicationLocales =
                LocaleList.forLanguageTags(newCode)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(newCode)
            )
        }

        viewModelScope.launch {
            _languagePreferenceCode.value = newCode
            AppPreferences.saveLanguagePreference(application, newCode)
        }
    }

    fun updateAppBarSearchQuery(query: String) {
        _appBarSearchQuery.value = query
    }
}
