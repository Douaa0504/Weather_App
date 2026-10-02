package com.erramidouaa.weatherapp.di

import android.app.Application
import com.erramidouaa.weatherapp.data.location.DefaultLocationTracker
import com.erramidouaa.weatherapp.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt responsable de la gestion des dépendances liées à la localisation.
 * Il définit comment fournir les instances nécessaires pour suivre la position de l'utilisateur.
 */
@Module
@InstallIn(SingletonComponent::class) // Ce module vit aussi longtemps que l'application (Singleton)
abstract class LocationModule {

    /**
     * @Binds est utilisé pour lier une interface à son implémentation concrète.
     * Ici, on dit à Hilt : "Chaque fois qu'une classe demande LocationTracker (Domain),
     * fournit lui DefaultLocationTracker (Data)".
     */
    @Binds
    @Singleton
    abstract fun bindLocationTracker(defaultLocationTracker: DefaultLocationTracker): LocationTracker

    companion object {
        /**
         * @Provides est utilisé pour les classes externes (comme Google SDK) que nous ne possédons pas.
         * FusedLocationProviderClient est l'API de Google pour obtenir la position GPS.
         */
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
            // Initialisation du client de localisation à partir des services Google Play
            return LocationServices.getFusedLocationProviderClient(app)
        }
    }
}