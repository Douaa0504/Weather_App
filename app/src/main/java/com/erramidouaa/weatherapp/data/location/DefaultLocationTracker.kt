package com.erramidouaa.weatherapp.data.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.erramidouaa.weatherapp.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Implémentation par défaut de l'interface LocationTracker.
 * Utilise Google Play Services (FusedLocationProviderClient) pour récupérer la position.
 */
class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {

        // 1. Vérification des permissions (Fine et Coarse Location)
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // 2. Vérification si les services de localisation (GPS/Réseau) sont activés sur l'appareil
        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        // Si les conditions ne sont pas remplies, on retourne null immédiatement
        if(!hasFineLocationPermission || !hasCoarseLocationPermission || !isGpsEnabled) {
            return null
        }

        // 3. Conversion de l'API de localisation (basée sur des Callbacks) en Coroutine suspendue
        return suspendCancellableCoroutine { continuation ->
            locationClient.lastLocation.apply {

                // Si la tâche est déjà terminée au moment de l'appel
                if(isComplete) {
                    if(isSuccessful) {
                        continuation.resume(result) // Succès : on retourne la localisation
                    } else {
                        continuation.resume(null) // Échec
                    }
                    return@suspendCancellableCoroutine
                }

                // Gestion des différents états de la requête asynchrone
                addOnSuccessListener {
                    continuation.resume(it)
                }
                addOnFailureListener {
                    continuation.resume(null)
                }
                addOnCanceledListener {
                    continuation.cancel()
                }
            }
        }
    }
}