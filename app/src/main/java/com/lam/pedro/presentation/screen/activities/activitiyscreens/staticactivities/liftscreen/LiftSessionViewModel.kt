package com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.liftscreen

import androidx.compose.runtime.MutableState
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.LiftSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import java.time.ZonedDateTime

class LiftSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING
    override lateinit var actualSession: LiftSession

    /*Define here the required permissions for the Health Connect usage*/
    override val permissions = setOf(

        /*
       * ExerciseSessionRecord
       * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * ExerciseCompletionGoal.RepetitionsGoal
        * */

        /*
        * ExercisePerformanceTarget.WeightTarget
        * */

        /*
        * TotalCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),

        /*
        * ActiveCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),

    )


    override suspend fun saveSession(activitySession: ActivitySession) {
        if (activitySession is LiftSession) {
            healthConnectManager.insertYogaSession(
                activityType,
                activitySession.startTime,
                activitySession.endTime,
                activitySession.title,
                activitySession.notes,
                activitySession.totalEnergy,
                activitySession.activeEnergy,
                activitySession.exerciseSegment,
                activitySession.exerciseLap
            )
        } else {
            throw IllegalArgumentException("Invalid session type for LiftSessionViewModel")
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
        distance: MutableState<Double>,
        exerciseRoute: List<ExerciseRoute.Location>
    ) {
        TODO("Not yet implemented")
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
class LiftSessionViewModel(val healthConnectManager: HealthConnectManager) :
    ViewModel() {
    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    val permissions = setOf(
        /*
       * ExerciseSessionRecord
       * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * ExerciseCompletionGoal.RepetitionsGoal
        * */

        /*
        * ExercisePerformanceTarget.WeightTarget
        * */

        /*
        * TotalCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),

        /*
        * ActiveCaloriesBurnedRecord
        * */
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
    )
    var weeklyAvg: MutableState<Mass?> = mutableStateOf(Mass.kilograms(0.0))
        private set

    var permissionsGranted = mutableStateOf(false)
        private set

    var readingsList: MutableState<List<WeightData>> = mutableStateOf(listOf())
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                //TODO: initial load of the data
            }
        }
    }



    /**
     * Provides permission check and error handling for Health Connect suspend function calls.
     *
     * Permissions are checked prior to execution of [block], and if all permissions aren't granted
     * the [block] won't be executed, and [permissionsGranted] will be set to false, which will
     * result in the UI showing the permissions button.
     *
     * Where an error is caught, of the type Health Connect is known to throw, [uiState] is set to
     * [UiState.Error], which results in the snackbar being used to show the error message.
     */
    private suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
        permissionsGranted.value = healthConnectManager.hasAllPermissions(permissions)
        uiState = try {
            if (permissionsGranted.value) {
                block()
            }
            UiState.Done
        } catch (remoteException: RemoteException) {
            UiState.Error(remoteException)
        } catch (securityException: SecurityException) {
            UiState.Error(securityException)
        } catch (ioException: IOException) {
            UiState.Error(ioException)
        } catch (illegalStateException: IllegalStateException) {
            UiState.Error(illegalStateException)
        }
    }

    sealed class UiState {
        object Uninitialized : UiState()
        object Done : UiState()

        // A random UUID is used in each Error object to allow errors to be uniquely identified,
        // and recomposition won't result in multiple snackbars.
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

 */

/*
class LiftSessionViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiftSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LiftSessionViewModel(
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

 */
