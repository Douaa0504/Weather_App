package com.erramidouaa.weatherapp.data.mapper

import com.erramidouaa.weatherapp.data.remote.CurrentWeatherResponse
import com.erramidouaa.weatherapp.data.remote.ForecastResponse
import com.erramidouaa.weatherapp.domain.model.WeatherData
import com.erramidouaa.weatherapp.domain.model.WeatherInfo
import java.text.SimpleDateFormat
import java.util.Locale

fun CurrentWeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        time = dt,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        humidity = main.humidity,
        windSpeed = wind.speed,
        visibility = visibility,
        pressure = main.pressure,
        description = weather.firstOrNull()?.description ?: "",
        main = weather.firstOrNull()?.main ?: "",
        icon = weather.firstOrNull()?.icon ?: ""
    )
}

fun ForecastResponse.toWeatherInfo(currentWeather: WeatherData): WeatherInfo {
    // OpenWeather 5-day forecast returns data every 3 hours.
    // We group by day for daily forecast and take the first few for hourly.
    
    val hourly = list.take(8).map { item ->
        WeatherData(
            time = item.dt,
            temperature = item.main.temp,
            feelsLike = item.main.feelsLike,
            humidity = item.main.humidity,
            windSpeed = item.wind.speed,
            visibility = null,
            pressure = item.main.pressure,
            description = item.weather.firstOrNull()?.description ?: "",
            main = item.weather.firstOrNull()?.main ?: "",
            icon = item.weather.firstOrNull()?.icon ?: ""
        )
    }

    val daily = list.filterIndexed { index, _ -> index % 8 == 0 }.map { item ->
        WeatherData(
            time = item.dt,
            temperature = item.main.temp,
            feelsLike = item.main.feelsLike,
            humidity = item.main.humidity,
            windSpeed = item.wind.speed,
            visibility = null,
            pressure = item.main.pressure,
            description = item.weather.firstOrNull()?.description ?: "",
            main = item.weather.firstOrNull()?.main ?: "",
            icon = item.weather.firstOrNull()?.icon ?: ""
        )
    }

    return WeatherInfo(
        current = currentWeather,
        dailyForecast = daily,
        hourlyForecast = hourly,
        city = "${city.name}, ${city.country ?: ""}"
    )
}
