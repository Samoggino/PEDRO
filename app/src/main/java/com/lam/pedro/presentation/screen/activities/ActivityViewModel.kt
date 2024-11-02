package com.lam.pedro.presentation.screen.activities

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SessionState
import com.lam.pedro.data.dateTimeWithOffsetOrDefault
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
import java.time.temporal.ChronoUnit
import java.util.UUID


abstract class ActivitySessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ViewModel() {

    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    /*Define here the required permissions for the Health Connect usage*/
    abstract val permissions: Set<String>

    var permissionsGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<ExerciseSession>> = mutableStateOf(listOf())
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

    private val _selectedSession = MutableLiveData<ExerciseSession>()
    val selectedSession: LiveData<ExerciseSession> = _selectedSession

    fun selectSession(session: ExerciseSession) {
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
                healthConnectManager.writeExerciseSession(start, adjustedEnd)
            }
        }
    }

    suspend fun saveExerciseTest(start: ZonedDateTime, finish: ZonedDateTime) {
        healthConnectManager.writeExerciseSession(start, finish)
    }


    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                readExerciseSessions()
            }
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startOfSession = ZonedDateTime.now()
                val endOfSession = startOfSession.plusMinutes(30) // imposta un esempio di durata

                // Avvia la sessione di esercizio e registra i dati necessari
                healthConnectManager.writeExerciseSession(startOfSession, endOfSession)
                readExerciseSessions()  // aggiorna la lista delle sessioni
            }
        }
    }

    suspend fun readExerciseSessions() {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()

        sessionsList.value = healthConnectManager
            .readExerciseSessions(startOfDay.toInstant(), now)
            .map { record ->
                val packageName = record.metadata.dataOrigin.packageName
                ExerciseSession(
                    startTime = dateTimeWithOffsetOrDefault(
                        record.startTime,
                        record.startZoneOffset
                    ),
                    endTime = dateTimeWithOffsetOrDefault(record.startTime, record.startZoneOffset),
                    id = record.metadata.id,
                    sourceAppInfo = healthConnectCompatibleApps[packageName],
                    title = record.title
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
