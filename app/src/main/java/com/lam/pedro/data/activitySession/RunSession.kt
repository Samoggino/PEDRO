package com.lam.pedro.data.activitySession

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import java.time.Instant
import kotlin.random.Random

data class RunSession(
    override val startTime: Instant,
    override val endTime: Instant,
    override val title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
    override val notes: String,
    val speedSamples: List<SpeedRecord.Sample>,
    //val stepsCadenceSamples: List<StepsCadenceRecord.Sample>,
    val stepsCount: Long,
    val totalEnergy: Energy,
    val activeEnergy: Energy,
    val distance: Length,
    val elevationGained: Length,
    val exerciseRoute: ExerciseRoute
) : ActivitySession(startTime, endTime, title, notes)

