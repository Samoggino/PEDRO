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

@Serializable
data class RunData(
    @Serializable(with = ActiveCaloriesBurnedRecordSerializer::class) val calories: ActiveCaloriesBurnedRecord,
    @Serializable(with = TotalCaloriesBurnedRecordSerializer::class) val totalCaloriesBurned: TotalCaloriesBurnedRecord?,
    @Serializable(with = DistanceRecordSerializer::class) val distanceRecord: DistanceRecord,
    @Serializable(with = ElevationGainedRecordSerializer::class) val elevationGainedRecord: ElevationGainedRecord,
    @Serializable(with = ExerciseRouteSerializer::class) val exerciseRoute: ExerciseRoute,
    @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedRecord: List<@Contextual SpeedRecord.Sample>,
    @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
    @Serializable(with = StepsRecordSerializer::class) val stepsRecord: StepsRecord,
)
