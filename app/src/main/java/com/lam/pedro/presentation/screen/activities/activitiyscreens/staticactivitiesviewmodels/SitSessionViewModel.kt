package com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivitiesviewmodels

import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.SitSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.serialization.SessionCreator
import java.time.ZonedDateTime

class SitSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    //override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR
    override lateinit var actualSession: SitSession

    override val activityEnum = ActivityEnum.SIT

    /*Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(


        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * HydrationRecord
        * */
        /*
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getWritePermission(HydrationRecord::class),

         */

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
        distance: Double,
        exerciseRoute: List<ExerciseRoute.Location>,
    ) {
        this.actualSession = SessionCreator.createSitSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            volume = Volume.liters(hydrationVolume)
        )
    }

    override suspend fun saveSession(activitySession: GenericActivity) {
        if (activitySession is SitSession) {
            healthConnectManager.insertSitSession(
                activityEnum.activityType,
                activitySession.basicActivity.startTime,
                activitySession.basicActivity.endTime,
                activitySession.basicActivity.title,
                activitySession.basicActivity.notes,
                activitySession.volume
            )
        } else {
            throw IllegalArgumentException("Invalid session type for SitSessionViewModel")
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