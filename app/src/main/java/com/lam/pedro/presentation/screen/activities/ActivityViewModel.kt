package com.lam.pedro.presentation.screen.activities

//import com.lam.pedro.data.ExerciseSession
import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SessionState
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.presentation.screen.activities.dynamicactivities.cyclingscreen.CycleSessionViewModel
import com.lam.pedro.presentation.screen.activities.dynamicactivities.runscreen.RunSessionViewModel
import com.lam.pedro.presentation.screen.activities.dynamicactivities.trainscreen.TrainSessionViewModel
import com.lam.pedro.presentation.screen.activities.dynamicactivities.walkscreen.WalkSessionViewModel
import com.lam.pedro.presentation.screen.activities.dynamicactivities.yogascreen.YogaSessionViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.drivescreen.DriveSessionViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.liftscreen.LiftSessionViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.listenscreen.ListenSessionViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.sitscreen.SitSessionViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.sleepscreen.SleepSessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID


abstract class ActivitySessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ViewModel() {

    abstract val actualSession: ActivitySession
    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    /*Define here the required permissions for the Health Connect usage*/
    abstract val permissions: Set<String>

    var permissionsGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<ActivitySession>> = mutableStateOf(listOf())
        private set

    abstract val activityType: Int

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    private var startTime: ZonedDateTime? = null
    private var endTime: ZonedDateTime? = null
    private var pausedTime: Duration = Duration.ZERO
    private var lastPauseTime: ZonedDateTime? = null
    private val _sessionState = MutableStateFlow(SessionState.IDLE)
    private var timerJob: Job? = null

    val sessionState: StateFlow<SessionState> get() = _sessionState

    private val _selectedSession = MutableLiveData<ActivitySession>()
    val selectedSession: LiveData<ActivitySession> = _selectedSession

    fun selectSession(session: ActivitySession) {
        //TODO: contact healthConnectManager to obtain the session data
        _selectedSession.value = session
    }


    private val _elapsedTime = MutableStateFlow(0) // Tempo in millisecondi
    val elapsedTime: StateFlow<Int> get() = _elapsedTime

    fun startSession() {
        startTime = ZonedDateTime.now()
        _sessionState.value = SessionState.RUNNING
        startTimer()
    }

    fun pauseSession() {
        if (_sessionState.value == SessionState.RUNNING) {
            lastPauseTime = ZonedDateTime.now()
            _sessionState.value = SessionState.PAUSED
            stopTimer()
        }
    }

    fun resumeSession() {
        if (_sessionState.value == SessionState.PAUSED) {
            lastPauseTime?.let {
                pausedTime += Duration.between(it, ZonedDateTime.now())
            }
            _sessionState.value = SessionState.RUNNING
            startTimer()
        }
    }

    suspend fun stopSession() {
        _sessionState.value = SessionState.STOPPED
        stopTimer()
        startTime?.let { endTime?.let { it1 -> saveExerciseTest(it, it1, 0, "Title", "Notes") } }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive && _sessionState.value == SessionState.RUNNING) {
                delay(10)
                _elapsedTime.value += 10
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /*
        private suspend fun saveExerciseRecord() {
            startTime?.let { start ->
                endTime?.let { end ->
                    val adjustedEnd = end - pausedTime // Sottrae il tempo totale in pausa
                    // Registra la sessione tramite Health Connect
                    healthConnectManager.insertExerciseSession(
                        start.toInstant(),
                        adjustedEnd.toInstant(),
                        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                        "My Run",
                        "Notes"
                    )

                }
            }
        }

     */

    private suspend fun saveExerciseTest(
        start: ZonedDateTime, finish: ZonedDateTime, exerciseType: Int, title: String, notes: String
    ) {
        healthConnectManager.insertExerciseSession(
            start.toInstant(), finish.toInstant(), exerciseType, title, notes
        )
    }

    abstract suspend fun saveSession(activitySession: ActivitySession)

    abstract fun createSession(
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
    )

    fun initialLoad(exerciseType: Int) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                fetchSessions(exerciseType)
            }
        }
    }

    fun startRecording(title: String, notes: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfSession = ZonedDateTime.now()
                val endOfSession = startOfSession.plusMinutes(30) // imposta un esempio di durata

                // Avvia la sessione di esercizio e registra i dati necessari
                healthConnectManager.insertExerciseSession(
                    startOfSession.toInstant(), endOfSession.toInstant(), activityType, title, notes
                )
                fetchSessions(activityType)  // aggiorna la lista delle sessioni
            }
        }
    }

    suspend fun fetchSessions(exerciseType: Int) {
        val start = Instant.EPOCH // 1st January 1970
        val now = Instant.now()

        sessionsList.value =
            healthConnectManager.fetchAndBuildActivitySession(start, now, exerciseType)

        Log.d("SESSION LIST", "${sessionsList.value}")
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

class GeneralActivityViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Dynamic activities
            modelClass.isAssignableFrom(CycleSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") CycleSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(RunSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") RunSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(TrainSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") TrainSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(WalkSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") WalkSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(YogaSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") YogaSessionViewModel(healthConnectManager) as T
            }

            // Static activities
            modelClass.isAssignableFrom(DriveSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") DriveSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(LiftSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") LiftSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(ListenSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") ListenSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(SitSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") SitSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(SleepSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST") SleepSessionViewModel(healthConnectManager) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

