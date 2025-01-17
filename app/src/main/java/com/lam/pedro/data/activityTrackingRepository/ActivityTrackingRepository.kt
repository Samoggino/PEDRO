package com.lam.pedro.data.activityTrackingRepository

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import org.maplibre.android.geometry.LatLng

object ActivityTrackingRepository {
    // Stati osservabili
    var steps = mutableFloatStateOf(0f)
    var averageSpeed = mutableDoubleStateOf(0.0)
    val distance = mutableDoubleStateOf(0.0)

    val speedSamples = mutableStateListOf<SpeedRecord.Sample>()
    val exerciseRoute = mutableStateListOf<ExerciseRoute.Location>()

    val speedCounter = mutableIntStateOf(0)
    val totalSpeed = mutableDoubleStateOf(0.0)
    val positions = mutableStateListOf<LatLng>()

    // Funzioni per aggiornare i dati
    fun updateSpeedCounter(newSpeed: Int) {
        speedCounter.intValue = newSpeed
    }

    fun updateTotalSpeed(newSpeed: Double) {
        totalSpeed.doubleValue = newSpeed
    }

    fun addPosition(position: LatLng) {
        positions.add(position)
    }
}
