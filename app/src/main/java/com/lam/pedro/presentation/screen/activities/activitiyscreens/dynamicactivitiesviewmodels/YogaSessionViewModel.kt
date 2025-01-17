package com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels

import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.YogaSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.serialization.SessionCreator
import com.lam.pedro.util.calculateAverageSpeed
import com.lam.pedro.util.calculateCalories
import java.time.ZonedDateTime


class YogaSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    override lateinit var actualSession: YogaSession

    override val activityEnum = ActivityEnum.YOGA

    /**Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(

        /**
         * ExerciseSessionRecord
         * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /**
         * ActiveCaloriesBurnedRecord
         * */
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),

        /**
         * ExerciseCompletionGoal.DurationGoal - permissions not needed, it doesn't use any sensors or personal data
         * */

        /**
         * TotalCaloriesBurnedRecord
         * */
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),

        /**
         * ExerciseLap - no permissions needed, it split exercise sessions into segments such as laps or exercise series
         * */

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
        val (totalCalories, activeCalories) = calculateCalories(
            profileViewModel.weight.value.toDouble(),
            profileViewModel.height.value.toDouble(),
            profileViewModel.age.value.toInt(),
            profileViewModel.sex.value,
            distance,
            steps.toInt(),
            duration,
            averageSpeed
        )
        this.actualSession = SessionCreator.createYogaSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            totalEnergy = Energy.calories(totalCalories),
            activeEnergy = Energy.calories(activeCalories),
            exerciseSegment = listOf(),//TODO
            exerciseLap = listOf()//TODO
        )
    }

    override suspend fun saveSession(activitySession: GenericActivity) {
        if (activitySession is YogaSession) {
            healthConnectManager.insertYogaSession(
                activityEnum.activityType,
                activitySession.basicActivity.startTime,
                activitySession.basicActivity.endTime,
                activitySession.basicActivity.title,
                activitySession.basicActivity.notes,
                activitySession.totalEnergy,
                activitySession.activeEnergy,
                activitySession.exerciseSegment,
                activitySession.exerciseLap
            )
        } else {
            throw IllegalArgumentException("Invalid session type for YogaSessionViewModel")
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
