package com.lam.pedro.util.geofence

interface GeofenceLocationCallback {
    fun onLocationUpdated(latitude: Double, longitude: Double)
}