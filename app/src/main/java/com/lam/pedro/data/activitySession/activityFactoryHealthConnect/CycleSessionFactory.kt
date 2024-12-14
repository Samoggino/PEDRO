package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.CycleSession

class CycleSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): ActivitySession {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val distance = getDistance(healthConnectClient, startTime, endTime)

        val speedSamples = getSpeedSamples(healthConnectClient, startTime, endTime)

        val totalCaloriesBurned = getTotalCaloriesBurned(healthConnectClient, startTime, endTime)

        val activeCaloriesBurned = getActiveCaloriesBurned(healthConnectClient, startTime, endTime)

        val exerciseRoute = getRoute(exerciseRecord)

        return CycleSession(
            title = exerciseRecord.title ?: "My Cycle #${exerciseRecord.hashCode()}",
            notes = exerciseRecord.notes ?: "",
            startTime = startTime,
            endTime = endTime,
            speedSamples = speedSamples,
            distance = distance,
            totalEnergy = totalCaloriesBurned,
            activeEnergy = activeCaloriesBurned,
            exerciseRoute = exerciseRoute ?: throw IllegalArgumentException("Exercise route is null")
        )
    }
}