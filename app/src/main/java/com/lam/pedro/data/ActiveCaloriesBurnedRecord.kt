@file:UseSerializers(
    EnergySerializer::class,
    InstantSerializer::class,
    ZoneOffsetSerializer::class
)

package com.lam.pedro.data

import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import com.lam.pedro.data.serializers.primitive.ZoneOffsetSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.ZoneOffset

@Serializable
data class ActiveCaloriesBurnedRecord(
    val startTime: Instant,
    val startZoneOffset: ZoneOffset? = null,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset? = null,
    val energy: Energy,
)
