package com.lam.pedro.data.activity.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity

class WalkSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val distance = getDistance(healthConnectClient, startTime, endTime)

        val speedSamples = getSpeedSamples(healthConnectClient, startTime, endTime)

        val steps = getSteps(healthConnectClient, startTime, endTime)

        val totalCaloriesBurned = getTotalCaloriesBurned(healthConnectClient, startTime, endTime)

        val activeCaloriesBurned = getActiveCaloriesBurned(healthConnectClient, startTime, endTime)

        val exerciseRoute = getRoute(exerciseRecord)

        return GenericActivity.WalkSession(
            GenericActivity.BasicActivity(
                title = exerciseRecord.title ?: "My Walk #${exerciseRecord.hashCode()}",
                notes = exerciseRecord.notes ?: "",
                startTime = startTime,
                endTime = endTime
            ),
            speedSamples = speedSamples,
            stepsCount = steps,
            distance = distance,
            totalEnergy = totalCaloriesBurned,
            activeEnergy = activeCaloriesBurned,
            exerciseRoute = exerciseRoute ?: ExerciseRoute(
                route = emptyList()
            )
        )
    }
}