@file:UseSerializers(
    EnergySerializer::class,
    ExerciseSegmentSerializer::class,
    ExerciseLapSerializer::class,
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.ExerciseSegmentSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class TrainSession(
    override val basicActivity: BasicActivity,
    val totalEnergy: Energy,
    val activeEnergy: Energy,

    val exerciseSegment: List<ExerciseSegment>,
    val exerciseLap: List<ExerciseLap>,
) : ActivityInterface(activityType = ActivityType.TRAIN)
