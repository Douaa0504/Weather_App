package com.erramidouaa.weatherapp.data.local

import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_cache WHERE id = 0")
    suspend fun getCurrentWeather(): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: WeatherEntity)

    @Query("SELECT * FROM forecast_cache WHERE isHourly = 1")
    suspend fun getHourlyForecast(): List<ForecastEntity>

    @Query("SELECT * FROM forecast_cache WHERE isHourly = 0")
    suspend fun getDailyForecast(): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastEntity>)

    @Query("DELETE FROM forecast_cache")
    suspend fun clearForecast()
}
