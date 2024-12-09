package com.lam.pedro.util

import androidx.compose.runtime.MutableFloatState
import org.maplibre.android.geometry.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun calculateTotalDistance(positions: List<LatLng>): Double {
    var totalDistance = 0.0

    for (i in 0 until positions.size - 1) {
        val start = positions[i]
        val end = positions[i + 1]

        // Calcola la distanza tra start e end usando l'Haversine formula
        totalDistance += haversineDistance(start, end)
    }

    return totalDistance
}

// Formula di Haversine per calcolare la distanza tra due coordinate geografiche
fun haversineDistance(start: LatLng, end: LatLng): Double {
    val R = 6371000.0 // Raggio della Terra in metri

    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLat = lat2 - lat1
    val dLon = lon2 - lon1

    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c // Distanza in metri
}