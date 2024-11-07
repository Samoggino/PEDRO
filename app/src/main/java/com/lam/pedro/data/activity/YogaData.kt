package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.activity.DurationGoalSerializer
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.activity.TotalCaloriesBurnedRecordSerializer
import kotlinx.serialization.Serializable

@Serializable
data class YogaData(
    @Serializable(with = ActiveCaloriesBurnedRecordSerializer::class) val calories: ActiveCaloriesBurnedRecord,
    @Serializable(with = DurationGoalSerializer::class) val durationGoal: ExerciseCompletionGoal.DurationGoal?,
    @Serializable(with = TotalCaloriesBurnedRecordSerializer::class) val totalCaloriesBurned: TotalCaloriesBurnedRecord?,
    @Serializable(with = ExerciseLapSerializer::class) val exerciseLap: ExerciseLap
)
