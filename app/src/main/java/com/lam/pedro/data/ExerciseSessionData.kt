package com.lam.pedro.data

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import kotlinx.serialization.Serializable
import java.time.Duration

@Serializable
data class ExerciseSessionDataSerializable(
    val uid: String,
    val totalActiveTime: Long? = null,  // in millisecondi
    val totalSteps: Long? = null,
    val totalDistance: Double? = null,  // in metri
    val totalEnergyBurned: Double? = null,  // in kilocalorie
    val minHeartRate: Long? = null,
    val maxHeartRate: Long? = null,
    val avgHeartRate: Long? = null
)

// deserialize
fun ExerciseSessionDataSerializable.deserialized() = ExerciseSessionData(
    uid = uid,
    totalActiveTime = totalActiveTime?.let { Duration.ofMillis(it) },
    totalSteps = totalSteps,
    totalDistance = totalDistance?.let { Length.meters(it) },
    totalEnergyBurned = totalEnergyBurned?.let { Energy.kilocalories(it) },
    minHeartRate = minHeartRate,
    maxHeartRate = maxHeartRate,
    avgHeartRate = avgHeartRate
)


data class ExerciseSessionData(
    val uid: String,
    val totalActiveTime: Duration? = null,
    val totalSteps: Long? = null,
    val totalDistance: Length? = null,
    val totalEnergyBurned: Energy? = null,
    val minHeartRate: Long? = null,
    val maxHeartRate: Long? = null,
    val avgHeartRate: Long? = null
) {
    // Conversione per serializzare nel database
    fun toSerializable() = ExerciseSessionDataSerializable(
        uid = uid,
        totalActiveTime = totalActiveTime?.toMillis(),
        totalSteps = totalSteps,
        totalDistance = totalDistance?.inMeters,
        totalEnergyBurned = totalEnergyBurned?.inKilocalories,
        minHeartRate = minHeartRate,
        maxHeartRate = maxHeartRate,
        avgHeartRate = avgHeartRate
    )
}