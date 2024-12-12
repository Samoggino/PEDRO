package com.lam.pedro.data

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseRouteResult
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.RunSession
import java.time.Instant

suspend fun buildActivitySession(
    healthConnectClient: HealthConnectClient,
    exerciseRecord: ExerciseSessionRecord
): RunSession {

    val startTime = exerciseRecord.startTime
    val endTime = exerciseRecord.endTime
    var title = exerciseRecord.title
    var notes = exerciseRecord.notes

    //TODO: teoricamente ritorna una lista con un solo oggetto in posizione 0

    val distanceRecord = healthConnectClient.readRecords(
        ReadRecordsRequest(
            recordType = DistanceRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
    ).records[0]
    Log.d("HALMA", "distanceRecord: $distanceRecord")
    val speedRecord = healthConnectClient.readRecords(
        ReadRecordsRequest(
            recordType = SpeedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
    ).records[0]
    Log.d("HALMA", "speedRecord: $speedRecord")
    val stepsRecord = healthConnectClient.readRecords(
        ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
    ).records[0]
    Log.d("HALMA", "stepsRecord: $stepsRecord")
    val totalCaloriesBurnedRecord = healthConnectClient.readRecords(
        ReadRecordsRequest(
            recordType = TotalCaloriesBurnedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
    ).records[0]
    Log.d("HALMA", "totalCaloriesBurnedRecord: $totalCaloriesBurnedRecord")
    val fetchActiveCaloriesBurnedRecord = healthConnectClient.readRecords(
        ReadRecordsRequest(
            recordType = ActiveCaloriesBurnedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
    ).records
    val activeCaloriesBurnedRecord = if (fetchActiveCaloriesBurnedRecord.isNotEmpty())
        fetchActiveCaloriesBurnedRecord[0]
    else
        null

    Log.d("HALMA", "activeCaloriesBurnedRecord: $activeCaloriesBurnedRecord")


    val exerciseRoute = if (exerciseRecord.exerciseRouteResult is ExerciseRouteResult.Data) {
        (exerciseRecord.exerciseRouteResult as ExerciseRouteResult.Data).exerciseRoute
    } else {
        null
    }

    return RunSession(
        title = title ?: "My Run #${exerciseRecord.hashCode()}",
        notes = notes?: "",
        startTime = startTime,
        endTime = endTime,
        speedSamples = speedRecord.samples,
        stepsCount = stepsRecord.count,
        totalEnergy = totalCaloriesBurnedRecord.energy,
        activeEnergy = activeCaloriesBurnedRecord?.energy ?: Energy.calories(0.0),
        distance = distanceRecord.distance,
        exerciseRoute = exerciseRoute
    )

}
