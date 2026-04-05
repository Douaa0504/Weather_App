package com.erramidouaa.weatherapp.presentation

import com.erramidouaa.weatherapp.domain.model.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
