package com.lam.pedro.data.activitySession

import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import java.time.Instant
import kotlin.random.Random

data class CycleSession(
    override val startTime: Instant,
    override val endTime: Instant,
    override val title: String = "My Cycle #${Random.nextInt(0, Int.MAX_VALUE)}",
    override val notes: String,

    val speedSamples: List<SpeedRecord.Sample>,
    val totalEnergy: Energy,
    val activeEnergy: Energy,
    val distance: Length,
    val exerciseRoute: ExerciseRoute
) : ActivitySession(startTime, endTime, title = title, notes = notes)

