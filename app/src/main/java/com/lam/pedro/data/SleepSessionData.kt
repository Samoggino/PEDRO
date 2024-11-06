@file:UseSerializers(
    InstantSerializer::class,
    ZoneOffsetSerializer::class,
    DurationSerializer::class,
    StageSerializer::class
)

package com.lam.pedro.data

import androidx.health.connect.client.records.SleepSessionRecord
import com.lam.pedro.data.serializers.DurationSerializer
import com.lam.pedro.data.serializers.InstantSerializer
import com.lam.pedro.data.serializers.StageSerializer
import com.lam.pedro.data.serializers.ZoneOffsetSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset


@Serializable
data class SleepSessionData(
    val uid: String,
    val title: String?,
    val notes: String?,
    val startTime: Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset?,
    val duration: Duration?,
    val stages: List<SleepSessionRecord.Stage>
)

// Classe serializzabile per Stage
@Serializable
data class StageSerializable(
    val startTime: Instant,
    val endTime: Instant,
    val stage: Int
)
