package com.lam.pedro.presentation.screen.activities

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
//import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SessionState
import com.lam.pedro.data.dateTimeWithOffsetOrDefault
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.reflect.KClass


abstract class ActivitySessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ViewModel() {

    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    /*Define here the required permissions for the Health Connect usage*/
    abstract val permissions: Set<String>

    var permissionsGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<ExerciseSessionRecord>> = mutableStateOf(listOf())
        private set

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

    private val _selectedSession = MutableLiveData<ExerciseSessionRecord>()
    val selectedSession: LiveData<ExerciseSessionRecord> = _selectedSession

    fun selectSession(session: ExerciseSessionRecord) {
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
        saveExerciseRecord()
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

    suspend fun saveExerciseTest(start: ZonedDateTime, finish: ZonedDateTime, exerciseType: Int, title: String, notes: String) {
        healthConnectManager.insertExerciseSession(
            start.toInstant(),
            finish.toInstant(),
            exerciseType,
            title,
            notes
        )
    }

    fun initialLoad(exerciseType: Int) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                fetchExerciseSessions(exerciseType)
            }
        }
    }

    fun startRecording(exerciseType: Int, title: String, notes: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfSession = ZonedDateTime.now()
                val endOfSession = startOfSession.plusMinutes(30) // imposta un esempio di durata

                // Avvia la sessione di esercizio e registra i dati necessari
                healthConnectManager.insertExerciseSession(startOfSession.toInstant(), endOfSession.toInstant(), exerciseType, title, notes)
                fetchExerciseSessions(exerciseType)  // aggiorna la lista delle sessioni
            }
        }
    }

    suspend fun fetchExerciseSessions(exerciseType: Int) {
        val start = Instant.EPOCH // 1st January 1970
        val now = Instant.now()

        // Chiamata al metodo del manager per leggere i dati da Health Connect
        val records = healthConnectManager.readExerciseSessions(start, now, exerciseType)

        // Mappa i record letti in un formato desiderato, se necessario
        sessionsList.value = records.map { record ->
            ExerciseSessionRecord(
                startTime = record.startTime,
                startZoneOffset = record.startZoneOffset,
                endTime = record.endTime,
                endZoneOffset = record.endZoneOffset,
                exerciseType = record.exerciseType,
                title = record.title,
                notes = record.notes,
                metadata = record.metadata,
                segments = record.segments,
                laps = record.laps
            )
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

class GeneralActivityViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Dynamic activities
            modelClass.isAssignableFrom(CycleSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                CycleSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(RunSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                RunSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(TrainSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                TrainSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(WalkSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                WalkSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(YogaSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                YogaSessionViewModel(healthConnectManager) as T
            }

            // Static activities
            modelClass.isAssignableFrom(DriveSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                DriveSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(LiftSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                LiftSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(ListenSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                ListenSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(SitSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SitSessionViewModel(healthConnectManager) as T
            }

            modelClass.isAssignableFrom(SleepSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SleepSessionViewModel(healthConnectManager) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

