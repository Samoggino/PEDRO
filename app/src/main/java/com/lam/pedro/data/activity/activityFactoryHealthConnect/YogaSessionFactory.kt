package com.lam.pedro.data.activity.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity

class YogaSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val totalCaloriesBurned = getTotalCaloriesBurned(healthConnectClient, startTime, endTime)

        val activeCaloriesBurned = getActiveCaloriesBurned(healthConnectClient, startTime, endTime)

        return GenericActivity.YogaSession(
            GenericActivity.BasicActivity(
                title = exerciseRecord.title ?: "My Walk #${exerciseRecord.hashCode()}",
                notes = exerciseRecord.notes ?: "",
                startTime = startTime,
                endTime = endTime
            ),
            totalEnergy = totalCaloriesBurned,
            activeEnergy = activeCaloriesBurned,
            exerciseSegment = exerciseRecord.segments,
            exerciseLap = exerciseRecord.laps
        )
    }
}