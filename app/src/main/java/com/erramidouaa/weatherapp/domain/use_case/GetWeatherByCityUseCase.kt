package com.erramidouaa.weatherapp.domain.use_case

import com.erramidouaa.weatherapp.domain.model.WeatherInfo
import com.erramidouaa.weatherapp.domain.repository.WeatherRepository
import com.erramidouaa.weatherapp.domain.util.Resource
import javax.inject.Inject

class GetWeatherByCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String, units: String): Resource<WeatherInfo> {
        return repository.getWeatherDataByCity(city, units)
    }
}
