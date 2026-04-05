package com.erramidouaa.weatherapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): ForecastResponse

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecastByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): ForecastResponse

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"
    }
}

data class CurrentWeatherResponse(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>,
    val wind: WindDto,
    val name: String,
    val visibility: Int?
)

data class ForecastResponse(
    val list: List<ForecastItemDto>,
    val city: CityDto
)

data class ForecastItemDto(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>,
    val wind: WindDto,
    val dt_txt: String?
)

data class MainDto(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double?,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int,
    val pressure: Int?
)

data class WindDto(
    val speed: Double
)

data class WeatherDescriptionDto(
    val main: String,
    val description: String,
    val icon: String
)

data class CityDto(
    val name: String,
    val country: String?
)
