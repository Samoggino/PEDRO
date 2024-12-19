package com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities

import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activity.GenericActivity.SleepSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.serialization.SessionCreator
import java.time.ZonedDateTime

class SleepSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

        //override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
    override lateinit var actualSession: SleepSession

    override val activityEnum = ActivityEnum.SLEEP

    /*Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(

        /*
        * ExerciseSessionRecord
        * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * SleepSessionRecord
        * */
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class)

        )

    override fun createSession(
        duration: Long,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        activityTitle: String,
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        steps: Float,
        hydrationVolume: Double,
        trainIntensity: String,
        yogaStyle: String,
        profileViewModel: ProfileViewModel,
        distance: MutableState<Double>,
        exerciseRoute: List<ExerciseRoute.Location>,
    ) {
        this.actualSession = SessionCreator.createSleepSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes
        )
    }

    override suspend fun saveSession(activitySession: GenericActivity) {
        if (activitySession is SleepSession) {
            healthConnectManager.insertSleepSession(
                activitySession.basicActivity.startTime,
                activitySession.basicActivity.endTime,
                activitySession.basicActivity.title,
                activitySession.basicActivity.notes,
            )
        } else {
            throw IllegalArgumentException("Invalid session type for SleepSessionViewModel")
        }
    }

    override var value: ActivitySessionViewModel?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun component1(): ActivitySessionViewModel? {
        TODO("Not yet implemented")
    }

    override fun component2(): (ActivitySessionViewModel?) -> Unit {
        TODO("Not yet implemented")
    }

}