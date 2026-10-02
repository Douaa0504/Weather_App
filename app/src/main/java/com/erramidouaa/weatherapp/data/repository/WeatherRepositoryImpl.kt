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

/**
 * Implémentation du repository Weather.
 * Cette classe gère la logique de récupération des données entre l'API (Remote) et la base de données (Local).
 */
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao
) : WeatherRepository {

    // Récupération de la clé API depuis les propriétés de configuration sécurisées
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    /**
     * Récupère la météo en fonction des coordonnées géographiques (Latitude, Longitude).
     */
    override suspend fun getWeatherData(lat: Double, lon: Double, units: String): Resource<WeatherInfo> {
        return withContext(Dispatchers.IO) { // Exécution dans le thread IO pour ne pas bloquer l'UI
            try {
                if (apiKey.isBlank()) return@withContext Resource.Error("API Key is missing")

                // Appel réseau simultané pour la météo actuelle et les prévisions
                val remoteCurrent = api.getCurrentWeatherByCoords(lat, lon, apiKey, units)
                val remoteForecast = api.getForecastByCoords(lat, lon, apiKey, units)

                // Transformation des DTO (données réseau) en modèles utilisables par l'application
                val currentWeather = remoteCurrent.toWeatherData()
                val weatherInfo = remoteForecast.toWeatherInfo(currentWeather)

                // Mise à jour du cache local (Room) pour le mode hors-ligne
                saveToCache(weatherInfo)

                Resource.Success(data = weatherInfo)
            } catch (e: Exception) {
                e.printStackTrace()
                // En cas d'erreur, on tente de récupérer les dernières données sauvegardées localement
                val cache = getFromCache()
                if (cache != null && cache is Resource.Success) {
                    cache
                } else {
                    handleException(e)
                }
            }
        }
    }

    /**
     * Récupère la météo en cherchant par le nom de la ville.
     */
    override suspend fun getWeatherDataByCity(city: String, units: String): Resource<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) return@withContext Resource.Error("API Key is missing")

                val remoteCurrent = api.getCurrentWeather(city, apiKey, units)
                val remoteForecast = api.getForecast(city, apiKey, units)

                val currentWeather = remoteCurrent.toWeatherData()
                val weatherInfo = remoteForecast.toWeatherInfo(currentWeather)

                // On met en cache également les recherches par ville
                saveToCache(weatherInfo)

                Resource.Success(data = weatherInfo)
            } catch (e: Exception) {
                e.printStackTrace()
                handleException(e)
            }
        }
    }

    /**
     * Centralisation de la gestion des erreurs (Réseau, Serveur, etc.).
     */
    private fun handleException(e: Exception): Resource<WeatherInfo> {
        return when (e) {
            is IOException -> Resource.Error("Erreur réseau. Veuillez vérifier votre connexion.")
            is HttpException -> {
                when (e.code()) {
                    401 -> Resource.Error("Clé API invalide. Vérifiez votre configuration.")
                    404 -> Resource.Error("Ville introuvable. Veuillez réessayer.")
                    else -> Resource.Error("Erreur serveur. Veuillez réessayer plus tard.")
                }
            }
            else -> Resource.Error(e.message ?: "Une erreur inconnue est survenue")
        }
    }

    /**
     * Sauvegarde les données météo dans Room (Base de données locale).
     */
    private suspend fun saveToCache(weatherInfo: WeatherInfo) {
        // Sauvegarde de la météo actuelle
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

        // Nettoyage et insertion des nouvelles prévisions (horaires et journalières)
        dao.clearForecast()
        val forecastEntities = weatherInfo.dailyForecast.map {
            it.toForecastEntity(isHourly = false)
        } + weatherInfo.hourlyForecast.map {
            it.toForecastEntity(isHourly = true)
        }
        dao.insertForecast(forecastEntities)
    }

    /**
     * Récupère les données depuis Room pour permettre l'affichage sans internet.
     */
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

    // --- Fonctions d'extension (Mappers) internes pour la conversion de données ---

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