package com.erramidouaa.weatherapp.presentation.navigation

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object Settings : Screen("settings")
}
