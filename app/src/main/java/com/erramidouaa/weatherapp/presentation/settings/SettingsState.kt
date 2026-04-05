package com.erramidouaa.weatherapp.presentation.settings

enum class AppTheme {
    LIGHT, DARK, AUTO
}

enum class AppLanguage(val code: String) {
    ENGLISH("en"), ARABIC("ar"), FRENCH("fr")
}

data class SettingsState(
    val theme: AppTheme = AppTheme.AUTO,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isMetric: Boolean = true
)
