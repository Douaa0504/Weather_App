package com.erramidouaa.weatherapp.data.mapper

import com.erramidouaa.weatherapp.data.remote.CurrentWeatherResponse
import com.erramidouaa.weatherapp.data.remote.ForecastResponse
import com.erramidouaa.weatherapp.domain.model.WeatherData
import com.erramidouaa.weatherapp.domain.model.WeatherInfo
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Mappers : Cette classe permet de transformer les objets de données brutes (DTO)
 * provenant de l'API en objets modèles utilisables par la couche Domain.
 */

// Convertit la réponse météo actuelle de l'API en modèle WeatherData (Domain)
fun CurrentWeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        time = dt,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        humidity = main.humidity,
        windSpeed = wind.speed,
        visibility = visibility,
        pressure = main.pressure,
        // Récupère la première description de la liste ou une chaîne vide si null
        description = weather.firstOrNull()?.description ?: "",
        main = weather.firstOrNull()?.main ?: "",
        icon = weather.firstOrNull()?.icon ?: ""
    )
}

// Convertit la réponse de prévision (Forecast) en un objet WeatherInfo complet
fun ForecastResponse.toWeatherInfo(currentWeather: WeatherData): WeatherInfo {

    /**
     * L'API OpenWeather renvoie des prévisions toutes les 3 heures.
     * Pour les prévisions horaires, nous prenons les 8 prochains créneaux (soit 24h).
     */
    val hourly = list.take(8).map { item ->
        WeatherData(
            time = item.dt,
            temperature = item.main.temp,
            feelsLike = item.main.feelsLike,
            humidity = item.main.humidity,
            windSpeed = item.wind.speed,
            visibility = null, // La visibilité n'est pas toujours disponible dans le forecast
            pressure = item.main.pressure,
            description = item.weather.firstOrNull()?.description ?: "",
            main = item.weather.firstOrNull()?.main ?: "",
            icon = item.weather.firstOrNull()?.icon ?: ""
        )
    }

    /**
     * Pour les prévisions quotidiennes :
     * Comme on reçoit une donnée toutes les 3h, on filtre pour ne prendre qu'un point
     * toutes les 24h (index % 8 == 0) afin d'obtenir un résumé par jour.
     */
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

    // Retourne l'objet final regroupant météo actuelle, horaire et quotidienne
    return WeatherInfo(
        current = currentWeather,
        dailyForecast = daily,
        hourlyForecast = hourly,
        city = "${city.name}, ${city.country ?: ""}"
    )
}