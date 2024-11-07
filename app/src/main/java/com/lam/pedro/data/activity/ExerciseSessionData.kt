@file:UseSerializers(
    DurationSerializer::class,
    LengthSerializer::class,
    EnergySerializer::class
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.primitive.DurationSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Duration

@Serializable
data class ExerciseSessionData(
    val uid: String,
    val totalActiveTime: Duration? = null,
    val totalSteps: Long? = null,
    val totalDistance: Length? = null,
    val totalEnergyBurned: Energy? = null,
    val minHeartRate: Long? = null,
    val maxHeartRate: Long? = null,
    val avgHeartRate: Long? = null
)