package com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.walkscreen

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
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.WalkSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.util.calculateYogaCalories
import java.time.ZonedDateTime

/*
class WalkSessionViewModel(val healthConnectManager: HealthConnectManager) :
    ViewModel() {

    /*Define here the required permissions for the Health Connect usage*/
    val permissions = setOf(

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

    var permissionsGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<SleepSessionData>> = mutableStateOf(listOf())
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                sessionsList.value = healthConnectManager.readSleepSessions()
            }
        }
    }

    fun saveSession(session: SleepSessionData) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                // Aggiorna la lista aggiungendo la nuova sessione
                sessionsList.value += session
                healthConnectManager.writeSleepSession(session) // salva la sessione su HealthConnect
                Log.d("SleepSessionViewModel", "Session saved")
            }
        }
    }


    fun addSleepData() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                sessionsList.value = healthConnectManager.readSleepSessions()
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

class WalkSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
    override lateinit var actualSession: WalkSession

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
        distance: MutableState<Double>,
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
        this.actualSession = WalkSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            speedSamples = speedSamples,
            stepsCount = steps.toLong(),
            totalEnergy = Energy.calories(totalCalories),
            activeEnergy = Energy.calories(activeCalories),
            distance = Length.meters(distance.value),
            exerciseRoute = ExerciseRoute(exerciseRoute)
        )
    }

    override suspend fun saveSession(activitySession: ActivitySession) {
        if (activitySession is WalkSession) {
            healthConnectManager.insertWalkSession(
                activityType,
                activitySession.startTime,
                activitySession.endTime,
                activitySession.title,
                activitySession.notes,
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
