package com.erramidouaa.weatherapp.domain.repository

import com.erramidouaa.weatherapp.domain.model.WeatherInfo
import com.erramidouaa.weatherapp.domain.util.Resource

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, lon: Double, units: String): Resource<WeatherInfo>
    suspend fun getWeatherDataByCity(city: String, units: String): Resource<WeatherInfo>
}
