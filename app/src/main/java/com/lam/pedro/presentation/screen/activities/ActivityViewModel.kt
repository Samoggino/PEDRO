package com.lam.pedro.presentation.screen.activities

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SessionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

open class ActivityViewModel(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    /*
    // Funzionalità di base per la gestione del timer e stato della sessione
    var permissionsGranted = mutableStateOf(false)
        protected set

    var sessionsList: MutableState<List<ExerciseSession>> = mutableStateOf(listOf())
        protected set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        protected set

    open val permissions = setOf{
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
        HealthPermission.getWritePermission(ExerciseSessionRecord::class)
    }

    private var startTime: ZonedDateTime? = null
    private var pausedTime: Duration = Duration.ZERO
    private var lastPauseTime: ZonedDateTime? = null
    private val _sessionState = MutableStateFlow(SessionState.IDLE)
    private var timerJob: Job? = null
    val sessionState: StateFlow<SessionState> get() = _sessionState

    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> get() = _elapsedTime

    // Avvio della sessione
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

    protected open suspend fun saveExerciseRecord() {
        // Implementazione generica per salvare il record
        startTime?.let { start ->
            val adjustedEnd = ZonedDateTime.now() - pausedTime
            healthConnectManager.writeExerciseSession(start, adjustedEnd)
        }
    }

    suspend fun tryWithPermissionsCheck(block: suspend () -> Unit) {
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

     */
}
