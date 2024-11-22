package com.lam.pedro.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Velocity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.lam.pedro.presentation.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import java.security.AccessController.checkPermission
import java.time.Instant

class SpeedTracker(
    private val context: Context
) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun trackSpeed(): Flow<SpeedRecord.Sample> = callbackFlow {
        if (!hasLocationPermissions()) {
            close(SecurityException("Location permissions are not granted."))
            return@callbackFlow
        }

        val locationListener = LocationListener { location ->
            val speedInMetersPerSecond = location.speed.toDouble()
            val velocity = Velocity.metersPerSecond(speedInMetersPerSecond)
            val sample = SpeedRecord.Sample(
                time = Instant.now(),
                speed = velocity
            )
            trySend(sample)
        }

        // Ensure location updates are requested on the main thread
        withContext(Dispatchers.Main) {
            val provider = LocationManager.GPS_PROVIDER
            val providerProperties = locationManager.getProviderProperties(provider)

            if (providerProperties != null) {
                try {
                    locationManager.requestLocationUpdates(provider, 5000L, 0f, locationListener)
                } catch (e: SecurityException) {
                    close(e)
                }
            }
        }

        awaitClose { locationManager.removeUpdates(locationListener) }
    }

    fun trackLocation(): Flow<ExerciseRoute.Location> = callbackFlow {
        if (!hasLocationPermissions()) {
            close(SecurityException("Location permissions are not granted."))
            return@callbackFlow
        }

        val locationListener = LocationListener { location ->
            val locationSample = ExerciseRoute.Location(
                time = Instant.now(),
                latitude = location.latitude,
                longitude = location.longitude
            )
            trySend(locationSample)
        }

        // Ensure location updates are requested on the main thread
        withContext(Dispatchers.Main) {
            val provider = LocationManager.GPS_PROVIDER
            val providerProperties = locationManager.getProviderProperties(provider)

            if (providerProperties != null) {
                try {
                    locationManager.requestLocationUpdates(provider, 5000L, 0f, locationListener)
                } catch (e: SecurityException) {
                    close(e)
                }
            }
        }

        awaitClose { locationManager.removeUpdates(locationListener) }
    }


    /*
    suspend fun collectSpeedSamples(): List<SpeedRecord.Sample> {

        if (!hasLocationPermissions()) {
            throw SecurityException("Location permissions are not granted.")
        }

        val speedSamples = mutableListOf<SpeedRecord.Sample>()

        // Create a Flow that tracks the speed over time
        trackSpeed().collect { sample ->
            speedSamples.add(sample)
        }

        return speedSamples // Return the list of samples after reaching the limit
    }

    private fun trackSpeed(): Flow<SpeedRecord.Sample> = callbackFlow {
        // Verifica che i permessi siano stati concessi
        if (!hasLocationPermissions()) {
            close(SecurityException("Location permissions are not granted."))
            return@callbackFlow
        }

        val locationListener = LocationListener { location ->
            val speedInMetersPerSecond = location.speed.toDouble()
            val velocity = Velocity.metersPerSecond(speedInMetersPerSecond)
            val sample = SpeedRecord.Sample(
                time = Instant.now(),
                speed = velocity
            )
            trySend(sample)
        }

        val provider = LocationManager.GPS_PROVIDER
        val providerProperties = locationManager.getProviderProperties(provider)

        if (providerProperties != null) {
            try {
                locationManager.requestLocationUpdates(provider, 5000L, 0f, locationListener)
            } catch (e: SecurityException) {
                // Gestisce l'eccezione nel caso in cui i permessi siano stati rimossi in tempo reale
                close(e)
            }
        }

        awaitClose { locationManager.removeUpdates(locationListener) }
    }

     */


    private fun hasLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }
}
