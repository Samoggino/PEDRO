package com.lam.pedro.util.geofence

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.Flow
import android.location.Location
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.LocationRequest
import com.lam.pedro.util.hasGeofenceLocationPermission
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationCallback
import kotlinx.coroutines.channels.awaitClose
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : GeofenceLocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (!context.hasGeofenceLocationPermission()) {
                throw GeofenceLocationClient.LocationException("Missing location permissions")
            }

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw GeofenceLocationClient.LocationException("GPS is disabled")
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(interval)
                .build()

            val coroutineScope = CoroutineScope(Dispatchers.Main)
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        coroutineScope.launch {
                            send(location)  // Assicurati che 'send' sia un'operazione compatibile con coroutines
                        }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}