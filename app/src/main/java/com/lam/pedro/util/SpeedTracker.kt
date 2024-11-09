package com.lam.pedro.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Velocity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.take
import java.time.Instant

/*
class SpeedTracker(
    private val locationProvider: FusedLocationProviderClient
) {

    suspend fun collectSpeedSamples(sampleLimit: Int): List<SpeedRecord.Sample> {
        val speedSamples = mutableListOf<SpeedRecord.Sample>()

        // Create a Flow that tracks the speed over time
        trackSpeed().take(sampleLimit).collect { sample ->
            speedSamples.add(sample)
        }

        return speedSamples // Return the list of samples after reaching the limit
    }

    private fun trackSpeed(): Flow<SpeedRecord.Sample> = callbackFlow {
        val locationRequest = LocationRequest.Builder(5000)
            .setIntervalMillis(5000)  // Define update interval
            .setQuality(LocationRequest.QUALITY_HIGH_ACCURACY)  // Maximum wait time for updates
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val speedInMetersPerSecond = location.speed.toDouble();
                    val velocity = Velocity.metersPerSecond(speedInMetersPerSecond);
                    val sample = SpeedRecord.Sample(
                        time = Instant.now(),
                        speed = velocity
                    )
                    trySend(sample) // Send each sample to the Flow
                }
            }
        }

        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        awaitClose { locationProvider.removeLocationUpdates(locationCallback) }
    }
}
 */

class SpeedTracker(
    private val context: Context
) {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }


    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    suspend fun collectSpeedSamples(): List<SpeedRecord.Sample> {
        val speedSamples = mutableListOf<SpeedRecord.Sample>()

        // Create a Flow that tracks the speed over time
        trackSpeed().collect { sample ->
            speedSamples.add(sample)
        }

        return speedSamples // Return the list of samples after reaching the limit
    }

    private fun trackSpeed(): Flow<SpeedRecord.Sample> = callbackFlow {
        val locationListener = LocationListener { location ->
            val speedInMetersPerSecond = location.speed.toDouble()
            val velocity = Velocity.metersPerSecond(speedInMetersPerSecond)
            val sample = SpeedRecord.Sample(
                time = Instant.now(),
                speed = velocity
            )
            trySend(sample) // Send each sample to the Flow
        }

        val provider = LocationManager.GPS_PROVIDER // Or NETWORK_PROVIDER depending on your use case
        val providerProperties = locationManager.getProviderProperties(provider)

        if (providerProperties != null) {
            // Only request updates if the provider is available
            locationManager.requestLocationUpdates(provider, 5000L, 0f, locationListener)
        }

        awaitClose { locationManager.removeUpdates(locationListener) }
    }
}
