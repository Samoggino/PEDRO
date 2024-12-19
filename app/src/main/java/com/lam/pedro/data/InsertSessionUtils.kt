package com.lam.pedro.data

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.presentation.TAG
import java.time.Instant
import java.time.ZoneOffset
import kotlin.random.Random

suspend fun insertRunSessionUtil(
    activityType: Int,
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant,
    title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
    notes: String,
    speedSamples: List<SpeedRecord.Sample>,
    //stepsCadenceSamples: List<StepsCadenceRecord.Sample>,
    stepsCount: Long,
    totalEnergy: Energy,
    activeEnergy: Energy,
    distance: Length,
    //elevationGained: Length,
    exerciseRoute: ExerciseRoute
) {

    // Create the ExerciseSessionRecord
    val exerciseSessionRecord = ExerciseSessionRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        exerciseType = activityType,
        title = title,
        notes = notes,
        exerciseRoute = exerciseRoute
    )


    val distanceRecord = DistanceRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        distance = distance
    )
    /*
    val elevationGainedRecord = ElevationGainedRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        elevation = elevationGained
    )
     */
    val speedRecord = SpeedRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        samples = speedSamples
    )
    /*
    val stepsCadenceRecord = StepsCadenceRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        samples = stepsCadenceSamples
    )

     */
    val stepsRecord = StepsRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        count = stepsCount
    )
    val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        energy = totalEnergy
    )
    // Create other records as needed
    val activeCaloriesBurnedRecord = ActiveCaloriesBurnedRecord(
        startTime = startTime,
        startZoneOffset = ZoneOffset.UTC,
        endTime = endTime,
        endZoneOffset = ZoneOffset.UTC,
        energy = activeEnergy
    )

    // Insert the records into Health Connect
    try {
        healthConnectClient.insertRecords(
            listOf(
                exerciseSessionRecord,
                distanceRecord,
                //elevationGainedRecord,
                speedRecord,
                //stepsCadenceRecord,
                stepsRecord,
                totalCaloriesBurnedRecord,
                activeCaloriesBurnedRecord
            )
        )
        Log.d(TAG, "Exercise session with route recorded successfully!")
    } catch (e: Exception) {
        Log.d(TAG, "Error recording exercise session with route: ${e.message}")
    }
}