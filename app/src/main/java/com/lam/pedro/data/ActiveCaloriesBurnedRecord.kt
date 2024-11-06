@file:UseSerializers(
    EnergySerializer::class,
    InstantSerializer::class,
    ZoneOffsetSerializer::class
)

package com.lam.pedro.data

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.serializers.EnergySerializer
import com.lam.pedro.data.serializers.InstantSerializer
import com.lam.pedro.data.serializers.ZoneOffsetSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.ZoneOffset

@Serializable
data class ActiveCaloriesBurnedRecordSerializable(
    val startTime: Instant,
    val startZoneOffset: ZoneOffset? = null,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset? = null,
    val energy: Energy,
)

// Converti alla classe di dominio
fun ActiveCaloriesBurnedRecordSerializable.deserialize() = ActiveCaloriesBurnedRecord(
    startTime = startTime,
    startZoneOffset = startZoneOffset,
    endTime = endTime,
    endZoneOffset = endZoneOffset,
    energy = energy,
)

fun ActiveCaloriesBurnedRecord.serialize() = ActiveCaloriesBurnedRecordSerializable(
    startTime = startTime,
    startZoneOffset = startZoneOffset,
    endTime = endTime,
    endZoneOffset = endZoneOffset,
    energy = energy,
)
