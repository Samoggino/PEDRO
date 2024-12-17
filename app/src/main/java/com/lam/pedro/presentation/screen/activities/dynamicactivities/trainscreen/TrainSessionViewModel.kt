package com.lam.pedro.presentation.screen.activities.dynamicactivities.trainscreen

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SleepSessionData
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.RunSession
import com.lam.pedro.data.activitySession.TrainSession
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.util.calculateAverageSpeed
import com.lam.pedro.util.calculateCalories
import com.lam.pedro.util.calculateTrainCalories
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime
import java.util.UUID

class TrainSessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ActivitySessionViewModel(healthConnectManager), MutableState<ActivitySessionViewModel?> {

    //private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    override val activityType: Int = ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS
    override lateinit var actualSession: TrainSession


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
        * ExerciseCompletionGoal.RepetitionsGoal - permissions not needed, it doesn't use any sensors or personal data
        * */

        /*
        *ExerciseCompletionGoal.DurationGoal - permissions not needed, it doesn't use any sensors or personal data
        * */

        /*
        * ExerciseLap - no permissions needed, it split exercise sessions into segments such as laps or exercise series
        * */

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
        val (totalCalories, activeCalories) = calculateTrainCalories(
            profileViewModel.weight.toDouble(),
            profileViewModel.height.toDouble(),
            profileViewModel.age.toInt(),
            profileViewModel.sex,
            duration,
            trainIntensity
        )
        this.actualSession = TrainSession(
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

    override suspend fun saveSession(activitySession: ActivitySession) {
        if (activitySession is TrainSession) {
            healthConnectManager.insertTrainSession(
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
            throw IllegalArgumentException("Invalid session type for TrainSessionViewModel")
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
class TrainSessionViewModel(val healthConnectManager: HealthConnectManager) :
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
        * ExerciseCompletionGoal.RepetitionsGoal - permissions not needed, it doesn't use any sensors or personal data
        * */

        /*
        *ExerciseCompletionGoal.DurationGoal - permissions not needed, it doesn't use any sensors or personal data
        * */

        /*
        * ExerciseLap - no permissions needed, it split exercise sessions into segments such as laps or exercise series
        * */

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

class TrainSessionViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrainSessionViewModel(
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

 */
