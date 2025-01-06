package com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels

import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.WalkSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.serialization.SessionCreator
import com.lam.pedro.util.calculateYogaCalories
import java.time.ZonedDateTime

class WalkSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    //override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
    override lateinit var actualSession: WalkSession

    override val activityEnum = ActivityEnum.WALK

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
        * ElevationGainedRecord
        * */
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        HealthPermission.getWritePermission(ElevationGainedRecord::class),

        /*
        * ExerciseRoute - it isn't a record, it uses GPS so it requires manifest permissions
        * */

        /*
        * SpeedRecord
        * */
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),

        /*
        * StepsCadenceRecord
        * */
        HealthPermission.getReadPermission(StepsCadenceRecord::class),
        HealthPermission.getWritePermission(StepsCadenceRecord::class),

        /*
        * StepsRecord
        * */
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),

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
        val (totalCalories, activeCalories) = calculateYogaCalories(
            profileViewModel.weight.toDouble(),
            profileViewModel.height.toDouble(),
            profileViewModel.age.toInt(),
            profileViewModel.sex,
            duration,
            yogaStyle
        )
        this.actualSession = SessionCreator.createWalkSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            speedSamples = speedSamples,
            stepsCount = steps.toLong(),
            totalEnergy = Energy.calories(totalCalories),
            activeEnergy = Energy.calories(activeCalories),
            distance = Length.meters(distance),
            exerciseRoute = ExerciseRoute(exerciseRoute)
        )
    }

    override suspend fun saveSession(activitySession: GenericActivity) {
        if (activitySession is WalkSession) {
            healthConnectManager.insertWalkSession(
                activityEnum.activityType,
                activitySession.basicActivity.startTime,
                activitySession.basicActivity.endTime,
                activitySession.basicActivity.title,
                activitySession.basicActivity.notes,
                activitySession.speedSamples,
                activitySession.stepsCount,
                activitySession.totalEnergy,
                activitySession.activeEnergy,
                activitySession.distance,
                activitySession.exerciseRoute
            )
        } else {
            throw IllegalArgumentException("Invalid session type for WalkSessionViewModel")
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

/*
class WalkSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager) {
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
        * ElevationGainedRecord
        * */
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        HealthPermission.getWritePermission(ElevationGainedRecord::class),

        /*
        * ExerciseRoute - it isn't a record, it uses GPS so it requires manifest permissions
        * */

        /*
        * SpeedRecord
        * */
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getWritePermission(SpeedRecord::class),

        /*
        * StepsCadenceRecord
        * */
        HealthPermission.getReadPermission(StepsCadenceRecord::class),
        HealthPermission.getWritePermission(StepsCadenceRecord::class),

        /*
        * StepsRecord
        * */
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),

        /*
        * TotalCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),

        )
    }

 */
/*
class WalkSessionViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalkSessionViewModel(
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

 */
