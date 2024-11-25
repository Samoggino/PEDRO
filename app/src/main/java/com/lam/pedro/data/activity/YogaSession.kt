@file:UseSerializers(
    EnergySerializer::class,
    ActivitySessionSerializer::class,
    InstantSerializer::class
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.serializers.lists.ExerciseLapListSerializer
import com.lam.pedro.data.serializers.lists.ExerciseSegmentListSerializer
import com.lam.pedro.data.serializers.primitive.ActivitySessionSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class YogaSession(
    val activitySession: ActivitySession,
    val totalEnergy: Energy,
    val activeEnergy: Energy,
    @Serializable(with = ExerciseSegmentListSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
    @Serializable(with = ExerciseLapListSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>
)

