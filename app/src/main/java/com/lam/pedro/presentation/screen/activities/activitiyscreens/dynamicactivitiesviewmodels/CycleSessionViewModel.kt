package com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels

import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.CyclingSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.serialization.SessionCreator
import com.lam.pedro.util.calculateAverageSpeed
import com.lam.pedro.util.calculateCyclingCalories
import java.time.ZonedDateTime


class CycleSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    //override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_BIKING
    override lateinit var actualSession: CyclingSession

    override val activityEnum = ActivityEnum.CYCLING

    /*Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(

        /*
       * ExerciseSessionRecord
       * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * ActiveCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),

        /*
        * DistanceRecord
        * */
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),

        /*
        * ExerciseRoute - it isn't a record, it uses GPS so it requires manifest permissions
        * */

        /*
        * CyclingPedalingCadenceRecord
        * */
        HealthPermission.getReadPermission(CyclingPedalingCadenceRecord::class),
        HealthPermission.getWritePermission(CyclingPedalingCadenceRecord::class),

        /*
        * SpeedRecord
        * */
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),

        /*
        * TotalCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),

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
        val averageSpeed = calculateAverageSpeed(speedSamples)
        val (totalCalories, activeCalories) = calculateCyclingCalories(
            profileViewModel.weight.toDouble(),
            profileViewModel.height.toDouble(),
            profileViewModel.age.toInt(),
            profileViewModel.sex,
            distance,
            duration,
            averageSpeed
        )
        this.actualSession = SessionCreator.createCyclingSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            speedSamples = speedSamples, // Non disponibile in ExerciseSessionRecord
            totalEnergy = Energy.calories(totalCalories), // Fallback
            activeEnergy = Energy.calories(activeCalories), // Fallback
            distance = Length.meters(distance), // Fallback
            exerciseRoute = ExerciseRoute(exerciseRoute) //Fallback
        )
    }

    override suspend fun saveSession(activitySession: GenericActivity) {
        if (activitySession is CyclingSession) {
            healthConnectManager.insertCycleSession(
                activityEnum.activityType,
                activitySession.basicActivity.startTime,
                activitySession.basicActivity.endTime,
                activitySession.basicActivity.title,
                activitySession.basicActivity.notes,
                activitySession.speedSamples,
                activitySession.totalEnergy,
                activitySession.activeEnergy,
                activitySession.distance,
                activitySession.exerciseRoute
            )
        } else {
            throw IllegalArgumentException("Invalid session type for CycleSessionViewModel")
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