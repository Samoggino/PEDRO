package com.lam.pedro.data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.activityFactoryHealthConnect.ActivitySessionFactoryFromHealthConnectProvider

suspend fun buildActivitySession(
    healthConnectClient: HealthConnectClient,
    exerciseRecord: ExerciseSessionRecord,
    exerciseType: Int
): GenericActivity {
    return ActivitySessionFactoryFromHealthConnectProvider.createSession(
        exerciseType,
        healthConnectClient,
        exerciseRecord
    )
}