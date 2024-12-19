package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.ListenSession

class ListenSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        return GenericActivity.ListenSession(
            GenericActivity.BasicActivity(
                title = exerciseRecord.title ?: "My Listen #${exerciseRecord.hashCode()}",
                notes = exerciseRecord.notes ?: "",
                startTime = startTime,
                endTime = endTime
            )
        )
    }
}