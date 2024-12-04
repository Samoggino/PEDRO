package com.lam.pedro.data.activity

import androidx.health.connect.client.records.SleepSessionRecord
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

data class SleepSessionData(
    val uid: String,
    val title: String?,
    val notes: String?,
    val startTime: Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset?,
    val duration: Duration?,
    val stages: List<SleepSessionRecord.Stage> = listOf()
)

