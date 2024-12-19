package com.lam.pedro.data.activitySession.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activitySession.ActivitySession

abstract class ActivitySessionFactoryFromHealthConnect {
    abstract suspend fun createSession(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord
    ): GenericActivity
}
