package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.SitSession

class SitSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): ActivitySession {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        val volume = getHydrationVolume(healthConnectClient, startTime, endTime)

        return SitSession(
            title = exerciseRecord.title ?: "My Sit #${exerciseRecord.hashCode()}",
            notes = exerciseRecord.notes ?: "",
            startTime = startTime,
            endTime = endTime,
            volume = volume,
        )
    }
}