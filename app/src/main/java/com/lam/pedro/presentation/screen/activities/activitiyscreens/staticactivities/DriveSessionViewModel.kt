package com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities

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
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.DriveSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import java.time.ZonedDateTime

class DriveSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    //override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_SURFING
    override lateinit var actualSession: DriveSession

    override val activityEnum = ActivityEnum.DRIVE

    /*Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(

        /*
        * ExerciseSessionRecord
        * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * DistanceRecord
        * */
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),

        /*
        * SpeedRecord
        * */
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),

        /*
        * ElevationGainedRecord
        * */
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        HealthPermission.getWritePermission(ElevationGainedRecord::class),

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
        this.actualSession = DriveSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            speedSamples = speedSamples,
            distance = Length.meters(distance.value),
            exerciseRoute = ExerciseRoute(exerciseRoute)
        )
        Log.d("ACTUAL SESSION", "$actualSession")
    }

    override suspend fun saveSession(activitySession: ActivitySession) {
        if (activitySession is DriveSession) {
            Log.d("SAVE SESSION", "$activitySession")
            healthConnectManager.insertDriveSession(
                activityEnum.activityType,
                activitySession.startTime,
                activitySession.endTime,
                activitySession.title,
                activitySession.notes,
                activitySession.speedSamples,
                activitySession.distance,
                activitySession.exerciseRoute
            )
        } else {
            throw IllegalArgumentException("Invalid session type for DriveSessionViewModel")
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