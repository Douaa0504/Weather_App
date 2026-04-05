package com.erramidouaa.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val city: String,
    val temperature: Double,
    val feelsLike: Double?,
    val description: String,
    val main: String,
    val humidity: Int,
    val windSpeed: Double,
    val visibility: Int?,
    val pressure: Int?,
    val icon: String,
    val lastUpdated: Long
)

@Entity(tableName = "forecast_cache")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dt: Long,
    val temp: Double,
    val description: String,
    val main: String,
    val icon: String,
    val isHourly: Boolean = false
)
