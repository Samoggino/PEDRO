package com.lam.pedro.util.geofence

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface GeofenceLocationClient {
    fun getLocationUpdates(interval : Long): Flow<Location>
    class LocationException(message: String): Exception()
}