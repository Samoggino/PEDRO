@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class,
    TotalCaloriesBurnedRecordSerializer::class,
    DurationGoalSerializer::class,
    ExerciseLapSerializer::class
)

package com.lam.pedro.data

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import com.lam.pedro.data.serializers.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.DurationGoalSerializer
import com.lam.pedro.data.serializers.ExerciseLapSerializer
import com.lam.pedro.data.serializers.TotalCaloriesBurnedRecordSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.UUID

@Serializable
data class YogaRecord(
    val uuidRecord: String? = UUID.randomUUID().toString(),
    @Serializable(with = ActiveCaloriesBurnedRecordSerializer::class) val calories: ActiveCaloriesBurnedRecord,
    @Serializable(with = DurationGoalSerializer::class) val durationGoal: ExerciseCompletionGoal.DurationGoal?,

    // FIXME: Funziona la serializzazione e deserializzazione dei parametri sopra, ma non quelli sotto

//    @Serializable(with = TotalCaloriesBurnedRecordSerializer::class) val totalCaloriesBurned: TotalCaloriesBurnedRecord?,
    //    val totalCaloriesBurned: TotalCaloriesBurnedRecord?,
//    @Serializable(with = ExerciseLapSerializer::class) val exerciseLap: ExerciseLap
    //    val exerciseLap: ExerciseLap
)
