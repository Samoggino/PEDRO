package com.lam.pedro.presentation.screen.activities.activitiyscreens.unknownactivityviewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.UnknownSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.serialization.SessionCreator
import java.time.ZonedDateTime

class UnknownSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    //override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_SURFING
    override lateinit var actualSession: UnknownSession

    override val activityEnum = ActivityEnum.UNKNOWN

    /*Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(

        /*
        * ExerciseSessionRecord
        * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        )

    override suspend fun saveSession(activitySession: GenericActivity) {
        if (activitySession is UnknownSession) {
            healthConnectManager.insertListenSession(
                activityEnum.activityType,
                activitySession.basicActivity.startTime,
                activitySession.basicActivity.endTime,
                activitySession.basicActivity.title,
                activitySession.basicActivity.notes
            )
        } else {
            throw IllegalArgumentException("Invalid session type for ListenSessionViewModel")
        }
    }

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
        distance: Double,
        exerciseRoute: List<ExerciseRoute.Location>
    ) {
        this.actualSession = SessionCreator.createUnknownSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes
        )
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