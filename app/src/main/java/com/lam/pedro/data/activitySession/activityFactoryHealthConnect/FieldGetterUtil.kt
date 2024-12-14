package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseRouteResult
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import java.time.Instant

suspend fun getSpeedSamples(
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): List<SpeedRecord.Sample> {
    val speedRecord = readFirstRecord<SpeedRecord>(healthConnectClient, startTime, endTime)
    return (speedRecord as? SpeedRecord)?.samples ?: emptyList()
}


suspend fun getDistance(
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): Length {
    val distanceRecord = readFirstRecord<DistanceRecord>(healthConnectClient, startTime, endTime)
    return (distanceRecord as? DistanceRecord)?.distance ?: Length.meters(0.0)
}


suspend fun getSteps(
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): Long {
    val stepsRecord = readFirstRecord<StepsRecord>(
        healthConnectClient, startTime, endTime
    )
    return (stepsRecord as? StepsRecord)?.count ?: 0L
}

suspend fun getTotalCaloriesBurned(
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): Energy {
    val totalCaloriesBurned = readFirstRecord<TotalCaloriesBurnedRecord>(healthConnectClient, startTime, endTime)
    return (totalCaloriesBurned as? TotalCaloriesBurnedRecord)?.energy ?: Energy.kilocalories(0.0)
}

suspend fun getActiveCaloriesBurned(
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): Energy {
    val activeCaloriesBurned = readFirstRecord<ActiveCaloriesBurnedRecord>(
        healthConnectClient, startTime, endTime
    )
    return (activeCaloriesBurned as? ActiveCaloriesBurnedRecord)?.energy ?: Energy.kilocalories(0.0)
}

fun getRoute(exerciseRecord: ExerciseSessionRecord): ExerciseRoute? {
    return if (exerciseRecord.exerciseRouteResult is ExerciseRouteResult.Data) {
        (exerciseRecord.exerciseRouteResult as ExerciseRouteResult.Data).exerciseRoute
    } else {
        null
    }
}

suspend fun getHydrationVolume(
    healthConnectClient: HealthConnectClient,
    startTime: Instant,
    endTime: Instant
): Volume {
    val hydrationVolume = readFirstRecord<HydrationRecord>(
        healthConnectClient, startTime, endTime
    )
    return (hydrationVolume as? HydrationRecord)?.volume ?: Volume.liters(0.0)
}