package com.erramidouaa.weatherapp.domain.model

data class WeatherInfo(
    val current: WeatherData,
    val dailyForecast: List<WeatherData>,
    val hourlyForecast: List<WeatherData>,
    val city: String
)

data class WeatherData(
    val time: Long,
    val temperature: Double,
    val feelsLike: Double? = null,
    val minTemp: Double? = null,
    val maxTemp: Double? = null,
    val humidity: Int,
    val windSpeed: Double,
    val visibility: Int? = null,
    val pressure: Int? = null,
    val description: String,
    val main: String,
    val icon: String
)
