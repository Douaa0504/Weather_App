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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(app: Application): WeatherDatabase {
        return Room.databaseBuilder(
            app,
            WeatherDatabase::class.java,
            "weather_db"
        )
        .fallbackToDestructiveMigration() // Fixes schema mismatch crash by clearing data
        .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi, dao: WeatherDao): WeatherRepository {
        return WeatherRepositoryImpl(api, dao)
    }
}
