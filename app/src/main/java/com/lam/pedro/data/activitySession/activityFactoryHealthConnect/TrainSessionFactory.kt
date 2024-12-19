package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.TrainSession

class TrainSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val totalCaloriesBurned = getTotalCaloriesBurned(healthConnectClient, startTime, endTime)

        val activeCaloriesBurned = getActiveCaloriesBurned(healthConnectClient, startTime, endTime)

        return GenericActivity.TrainSession(
            GenericActivity.BasicActivity(
                title = exerciseRecord.title ?: "My Train #${exerciseRecord.hashCode()}",
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