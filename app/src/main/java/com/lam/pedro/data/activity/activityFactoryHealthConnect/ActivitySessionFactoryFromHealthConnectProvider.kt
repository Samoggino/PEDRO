package com.lam.pedro.data.activity.activityFactoryHealthConnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity

object ActivitySessionFactoryFromHealthConnectProvider {
    private val factories: Map<Int, ActivitySessionFactoryFromHealthConnect> = mapOf(
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING to RunSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING to WalkSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING to CycleSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_SURFING to DriveSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING to ListenSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR to SitSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_WATER_POLO to SleepSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS to TrainSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA to YogaSessionFactory(),
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT to UnknownSessionFactory()
    )

    suspend fun createSession(
        exerciseType: Int,
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord?
    ): GenericActivity {
        return exerciseRecord?.let {
            factories[exerciseType]?.createSession(healthConnectClient,
                it
            )
        }
            ?: throw IllegalArgumentException("Unknown exercise type: $exerciseType")
    }
}
