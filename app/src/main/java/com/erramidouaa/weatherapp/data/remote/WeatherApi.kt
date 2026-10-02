package com.erramidouaa.weatherapp.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit définissant les points d'accès (endpoints) de l'API OpenWeather.
 * Cette interface gère les requêtes réseau pour la météo actuelle et les prévisions.
 */
interface WeatherApi {

    /**
     * Récupère la météo actuelle pour une ville spécifique par son nom.
     */
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): CurrentWeatherResponse

    /**
     * Récupère les prévisions météorologiques (5 jours / 3 heures) par nom de ville.
     */
    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): ForecastResponse

    /**
     * Récupère la météo actuelle en utilisant les coordonnées géographiques (Latitude/Longitude).
     */
    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): CurrentWeatherResponse

    /**
     * Récupère les prévisions météorologiques en utilisant les coordonnées géographiques.
     */
    @GET("data/2.5/forecast")
    suspend fun getForecastByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): ForecastResponse

    companion object {
        // URL de base de l'API OpenWeather
        const val BASE_URL = "https://api.openweathermap.org/"
    }
}

/**
 * Objet de Transfert de Données (DTO) pour la réponse de la météo actuelle.
 */
data class CurrentWeatherResponse(
    val dt: Long, // Horodatage (Timestamp)
    val main: MainDto, // Données principales (température, humidité)
    val weather: List<WeatherDescriptionDto>, // Liste des descriptions météo
    val wind: WindDto, // Informations sur le vent
    val name: String, // Nom de la ville
    val visibility: Int? // Visibilité en mètres
)

/**
 * DTO pour la réponse des prévisions météorologiques.
 */
data class ForecastResponse(
    val list: List<ForecastItemDto>, // Liste des points de prévision
    val city: CityDto // Informations sur la ville concernée
)

/**
 * Représente un point spécifique dans la liste des prévisions.
 */
data class ForecastItemDto(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>,
    val wind: WindDto,
    val dt_txt: String? // Date et heure au format texte (ex: "2026-04-21 15:00:00")
)

/**
 * DTO contenant les indicateurs physiques principaux.
 */
data class MainDto(
    val temp: Double, // Température actuelle
    @SerializedName("feels_like")
    val feelsLike: Double?, // Température ressentie
    val temp_min: Double, // Température minimale
    val temp_max: Double, // Température maximale
    val humidity: Int, // Taux d'humidité en %
    val pressure: Int? // Pression atmosphérique
)

/**
 * DTO pour les données du vent.
 */
data class WindDto(
    val speed: Double // Vitesse du vent
)

/**
 * DTO décrivant l'état visuel de la météo (nuageux, ensoleillé, etc.).
 */
data class WeatherDescriptionDto(
    val main: String, // Catégorie météo (ex: Rain, Clouds)
    val description: String, // Description détaillée
    val icon: String // ID de l'icône pour l'affichage graphique
)

/**
 * DTO contenant les informations géographiques de la ville.
 */
data class CityDto(
    val name: String,
    val country: String? // Code pays (ex: MA, FR)
)