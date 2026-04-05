package com.erramidouaa.weatherapp.domain.use_case

import com.erramidouaa.weatherapp.domain.model.WeatherInfo
import com.erramidouaa.weatherapp.domain.repository.WeatherRepository
import com.erramidouaa.weatherapp.domain.util.Resource
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, units: String): Resource<WeatherInfo> {
        return repository.getWeatherData(lat, lon, units)
    }
}
