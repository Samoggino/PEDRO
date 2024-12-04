@file:UseSerializers(
    EnergySerializer::class,
    InstantSerializer::class
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.serializers.lists.ListExerciseLapSerializer
import com.lam.pedro.data.serializers.lists.ListExerciseSegmentSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

