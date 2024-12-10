package com.lam.pedro.data.activitySession

import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.units.Energy
import java.time.Instant
import kotlin.random.Random

data class LiftSession(
    override val startTime: Instant,
    override val endTime: Instant,
    override val title: String = "My Lift #${Random.nextInt(0, Int.MAX_VALUE)}",
    override val notes: String,
    val totalEnergy: Energy,
    val activeEnergy: Energy,
    val exerciseSegment: List<ExerciseSegment>,
    val exerciseLap: List<ExerciseLap>
) : ActivitySession(startTime, endTime, title = title, notes = notes)

