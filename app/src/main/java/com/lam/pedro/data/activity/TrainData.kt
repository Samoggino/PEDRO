@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class,
    DurationGoalSerializer::class,
    ExerciseLapSerializer::class,
    TotalCaloriesBurnedRecordSerializer::class,
    RepetitionsGoalSerializer::class
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.activity.DurationGoalSerializer
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.activity.TotalCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.primitive.RepetitionsGoalSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class TrainData(
    val activeCaloriesBurnedRecord: ActiveCaloriesBurnedRecord,
    val repetitionsGoal: ExerciseCompletionGoal.RepetitionsGoal,
    val durationGoal: ExerciseCompletionGoal.DurationGoal,
    val exerciseLap: ExerciseLap,
    val totalCaloriesBurnedRecord: TotalCaloriesBurnedRecord
)
