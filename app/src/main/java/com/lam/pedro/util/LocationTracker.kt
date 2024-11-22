package com.lam.pedro.util

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import androidx.health.connect.client.records.ExerciseRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.time.Instant

class LocationTracker(
    private val context: Context
) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun trackLocation(): Flow<ExerciseRoute.Location> = callbackFlow {
        if (!hasLocationPermissions(context)) {
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
}