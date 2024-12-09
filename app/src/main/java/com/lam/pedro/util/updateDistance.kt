package com.lam.pedro.util

import androidx.compose.runtime.MutableState
import org.maplibre.android.geometry.LatLng

fun updateDistance(distance: MutableState<Double>, positions: List<LatLng>, newLatLng: LatLng) {
    if (positions.isNotEmpty()) {
        val lastPosition = positions.last() // Prendi l'ultimo elemento
        val newDistance = haversineDistance(lastPosition, newLatLng) // Calcola la distanza

        // Aggiungi la distanza calcolata al valore corrente
        distance.value += newDistance
    }
}