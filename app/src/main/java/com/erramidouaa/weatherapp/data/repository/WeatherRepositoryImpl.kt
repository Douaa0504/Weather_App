package com.erramidouaa.weatherapp.data.repository

import com.erramidouaa.weatherapp.BuildConfig
import com.erramidouaa.weatherapp.data.local.ForecastEntity
import com.erramidouaa.weatherapp.data.local.WeatherDao
import com.erramidouaa.weatherapp.data.local.WeatherEntity
import com.erramidouaa.weatherapp.data.mapper.toWeatherData
import com.erramidouaa.weatherapp.data.mapper.toWeatherInfo
import com.erramidouaa.weatherapp.data.remote.WeatherApi
import com.erramidouaa.weatherapp.domain.model.WeatherData
import com.erramidouaa.weatherapp.domain.model.WeatherInfo
import com.erramidouaa.weatherapp.domain.repository.WeatherRepository
import com.erramidouaa.weatherapp.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
) : WeatherRepository {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    override suspend fun getWeatherData(lat: Double, lon: Double, units: String): Resource<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) return@withContext Resource.Error("API Key is missing")

                val remoteCurrent = api.getCurrentWeatherByCoords(lat, lon, apiKey, units)
                val remoteForecast = api.getForecastByCoords(lat, lon, apiKey, units)
                
                val currentWeather = remoteCurrent.toWeatherData()
                val weatherInfo = remoteForecast.toWeatherInfo(currentWeather)
                
                saveToCache(weatherInfo)
                
                Resource.Success(data = weatherInfo)
            } catch (e: Exception) {
                e.printStackTrace()
                val cache = getFromCache()
                if (cache != null && cache is Resource.Success) {
                    cache
                } else {
                    handleException(e)
                }
            }
        }
    }

    override suspend fun getWeatherDataByCity(city: String, units: String): Resource<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) return@withContext Resource.Error("API Key is missing")

                val remoteCurrent = api.getCurrentWeather(city, apiKey, units)
                val remoteForecast = api.getForecast(city, apiKey, units)
                
                val currentWeather = remoteCurrent.toWeatherData()
                val weatherInfo = remoteForecast.toWeatherInfo(currentWeather)
                
                saveToCache(weatherInfo)
                
                Resource.Success(data = weatherInfo)
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            }
        }
    }

    private fun handleException(e: Exception): Resource<WeatherInfo> {
        return when (e) {
            is IOException -> Resource.Error("Network error. Please check your internet connection.")
            is HttpException -> {
                when (e.code()) {
                    401 -> Resource.Error("Invalid API key. Please check your configuration.")
                    404 -> Resource.Error("City not found. Please try another name.")
                    else -> Resource.Error("Server error. Please try again later.")
                }
            }
            else -> Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    private suspend fun saveToCache(weatherInfo: WeatherInfo) {
        dao.insertCurrentWeather(
            WeatherEntity(
                city = weatherInfo.city,
                temperature = weatherInfo.current.temperature,
                feelsLike = weatherInfo.current.feelsLike,
                description = weatherInfo.current.description,
                main = weatherInfo.current.main,
                humidity = weatherInfo.current.humidity,
                windSpeed = weatherInfo.current.windSpeed,
                visibility = weatherInfo.current.visibility,
                pressure = weatherInfo.current.pressure,
                icon = weatherInfo.current.icon,
                lastUpdated = System.currentTimeMillis()
            )
        )
        dao.clearForecast()
        val forecastEntities = weatherInfo.dailyForecast.map { 
            it.toForecastEntity(isHourly = false) 
        } + weatherInfo.hourlyForecast.map { 
            it.toForecastEntity(isHourly = true) 
        }
        dao.insertForecast(forecastEntities)
    }

    private suspend fun getFromCache(): Resource<WeatherInfo>? {
        val current = dao.getCurrentWeather() ?: return null
        val hourly = dao.getHourlyForecast()
        val daily = dao.getDailyForecast()
        
        return Resource.Success(
            data = WeatherInfo(
                current = current.toWeatherData(),
                dailyForecast = daily.map { it.toWeatherData() },
                hourlyForecast = hourly.map { it.toWeatherData() },
                city = current.city
            )
        )
    }

    private fun WeatherData.toForecastEntity(isHourly: Boolean): ForecastEntity {
        return ForecastEntity(
            dt = time,
            temp = temperature,
            description = description,
            main = main,
            icon = icon,
            isHourly = isHourly
        )
    }

    private fun WeatherEntity.toWeatherData(): WeatherData {
        return WeatherData(
            time = lastUpdated,
            temperature = temperature,
            feelsLike = feelsLike,
            humidity = humidity,
            windSpeed = windSpeed,
            visibility = visibility,
            pressure = pressure,
            description = description,
            main = main,
            icon = icon
        )
    }

    private fun ForecastEntity.toWeatherData(): WeatherData {
        return WeatherData(
            time = dt,
            temperature = temp,
            humidity = 0,
            windSpeed = 0.0,
            description = description,
            main = main,
            icon = icon
        )
    }
}
