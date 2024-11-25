@file:UseSerializers(
    EnergySerializer::class,
    LengthSerializer::class,
    SpeedRecordSerializer::class,
    StepsRecordSerializer::class,
    TotalCaloriesBurnedRecordSerializer::class,
    ExerciseRouteSerializer::class,
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.activity.TotalCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.lists.ListSpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import com.lam.pedro.data.serializers.primitive.SpeedRecordSerializer
import com.lam.pedro.data.serializers.primitive.StepsRecordSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class RunSession(
    val activitySession: ActivitySession,
    @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
//    @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
    val stepsCount: Long,
    val totalEnergy: Energy,
    val activeEnergy: Energy,
    val distance: Length,
    val elevationGained: Length,
    val exerciseRoute: ExerciseRoute,
)

