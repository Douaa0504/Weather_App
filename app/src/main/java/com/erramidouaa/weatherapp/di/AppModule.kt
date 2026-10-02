package com.erramidouaa.weatherapp.di

import android.app.Application
import androidx.room.Room
import com.erramidouaa.weatherapp.data.local.WeatherDao
import com.erramidouaa.weatherapp.data.local.WeatherDatabase
import com.erramidouaa.weatherapp.data.remote.WeatherApi
import com.erramidouaa.weatherapp.data.repository.WeatherRepositoryImpl
import com.erramidouaa.weatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Module Hilt pour la gestion de l'injection de dépendances au niveau de l'application.
 * Les instances fournies ici vivront aussi longtemps que l'application (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Fournit l'instance de Retrofit pour les appels API.
     * Inclut un client HTTP avec logging pour faciliter le débogage en mode développement.
     */
    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        // Intercepteur pour afficher les logs des requêtes et réponses réseau dans Logcat
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Convertit le JSON en objets Kotlin
            .client(client)
            .build()
            .create(WeatherApi::class.java)
    }

    /**
     * Fournit l'instance de la base de données Room.
     * Gère la persistance des données locales pour le mode hors-ligne.
     */
    @Provides
    @Singleton
    fun provideWeatherDatabase(app: Application): WeatherDatabase {
        return Room.databaseBuilder(
            app,
            WeatherDatabase::class.java,
            "weather_db"
        )
            // Permet de recréer la table si le schéma change sans migration complexe
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Fournit le DAO (Data Access Object) pour effectuer les requêtes SQL.
     */
    @Provides
    @Singleton
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao {
        return db.dao
    }

    /**
     * Fournit l'implémentation du Repository.
     * C'est ici que le pont entre la source de données distante (API) et locale (DAO) est établi.
     */
    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi, dao: WeatherDao): WeatherRepository {
        return WeatherRepositoryImpl(api, dao)
    }
}