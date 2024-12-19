package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.DriveSession
import com.lam.pedro.data.activitySession.RunSession

class DriveSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val distance = getDistance(healthConnectClient, startTime, endTime)

        val speedSamples = getSpeedSamples(healthConnectClient, startTime, endTime)

        val exerciseRoute = getRoute(exerciseRecord)

        val basicActivity = GenericActivity.BasicActivity(
            title = exerciseRecord.title ?: "My Drive #${exerciseRecord.hashCode()}",
            notes = exerciseRecord.notes ?: "",
            startTime = startTime,
            endTime = endTime,
        )

        return GenericActivity.DriveSession(
            basicActivity,
            speedSamples = speedSamples,
            distance = distance,
            exerciseRoute = exerciseRoute ?: throw IllegalArgumentException("Exercise route is null")
        )
    }
}
