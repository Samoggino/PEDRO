package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.SleepSession

class SleepSessionFactory : ActivitySessionFactoryFromHealthConnect() {
    override suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity {
        val startTime = exerciseRecord.startTime
        val endTime = exerciseRecord.endTime

        return GenericActivity.SleepSession(
            GenericActivity.BasicActivity(
                title = exerciseRecord.title ?: "My Sleep #${exerciseRecord.hashCode()}",
                notes = exerciseRecord.notes ?: "",
                startTime = startTime,
                endTime = endTime
            )
        )
    }
}