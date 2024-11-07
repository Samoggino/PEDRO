@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class,
    DistanceRecordSerializer::class,
    ElevationGainedRecordSerializer::class,
    ExerciseRouteSerializer::class,
    TotalCaloriesBurnedRecordSerializer::class,
    StepsRecordSerializer::class
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.activity.DistanceRecordSerializer
import com.lam.pedro.data.serializers.activity.ElevationGainedRecordSerializer
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.activity.TotalCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.lists.ListSpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.lists.ListStepsCadenceSampleSerializer
import com.lam.pedro.data.serializers.primitive.StepsRecordSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class RunData(
    val calories: ActiveCaloriesBurnedRecord,
    val totalCaloriesBurned: TotalCaloriesBurnedRecord?,
    val distanceRecord: DistanceRecord,
    val elevationGainedRecord: ElevationGainedRecord,
    val exerciseRoute: ExerciseRoute,
    @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedRecord: List<@Contextual SpeedRecord.Sample>,
    @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
    val stepsRecord: StepsRecord,
)
