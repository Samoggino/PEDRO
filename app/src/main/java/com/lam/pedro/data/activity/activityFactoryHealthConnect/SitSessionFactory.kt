package com.lam.pedro.data.activity.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity

class SitSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val volume = getHydrationVolume(healthConnectClient, startTime, endTime)

        return GenericActivity.SitSession(
            GenericActivity.BasicActivity(
                title = exerciseRecord.title ?: "My Sit #${exerciseRecord.hashCode()}",
                notes = exerciseRecord.notes ?: "",
                startTime = startTime,
                endTime = endTime
            ),
            volume = volume,
        )
    }
}