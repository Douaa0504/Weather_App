package com.erramidouaa.weatherapp.presentation.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun onThemeChange(theme: AppTheme) {
        _state.update { it.copy(theme = theme) }
    }

    fun onLanguageChange(language: AppLanguage) {
        _state.update { it.copy(language = language) }
    }

    fun onUnitChange(isMetric: Boolean) {
        _state.update { it.copy(isMetric = isMetric) }
    }
}
