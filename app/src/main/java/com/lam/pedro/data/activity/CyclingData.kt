@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class,
    DistanceRecordSerializer::class,
    ElevationGainedRecordSerializer::class,
    ExerciseRouteSerializer::class,
    TotalCaloriesBurnedRecordSerializer::class,
    StepsRecordSerializer::class,
    SpeedRecordSerializer::class,
    CyclingPedalingCadenceRecordSerializer::class
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.activity.DistanceRecordSerializer
import com.lam.pedro.data.serializers.activity.ElevationGainedRecordSerializer
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.activity.TotalCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.primitive.CyclingPedalingCadenceRecordSerializer
import com.lam.pedro.data.serializers.primitive.SpeedRecordSerializer
import com.lam.pedro.data.serializers.primitive.StepsRecordSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


/***
 * CYCLING:
 *
 * val permissions = setOf(
 *         /*
 *         * ActiveCaloriesBurnedRecord
 *         * */
 *
 *         /*
 *         * DistanceRecord
 *         * */
 *
 *         /*
 *         * ElevationGainedRecord
 *         * */
 *
 *         /*
 *         * ExerciseRoute - it isn't a record, it uses GPS so it requires manifest permissions
 *         * */
 *
 *         /*
 *         * CyclingPedalingCadenceRecord
 *         * */
 *
 *         /*
 *         * SpeedRecord
 *         * */
 *
 *         /*
 *         * TotalCaloriesBurnedRecord
 *         * */
 *
 *
 *         )
 */


@Serializable
data class CyclingData(
    val calories: ActiveCaloriesBurnedRecord,
    val distanceRecord: DistanceRecord,
    val elevationGainedRecord: ElevationGainedRecord,
    val exerciseRoute: ExerciseRoute,
    val pedalingCadenceRecord: CyclingPedalingCadenceRecord,
    val speedRecord: SpeedRecord,
    val totalCaloriesBurned: TotalCaloriesBurnedRecord?
)