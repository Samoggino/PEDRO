package com.lam.pedro.presentation.screen.activities.staticactivities.sleepscreen

import android.os.RemoteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SleepSessionData
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class SleepSessionViewModel(val healthConnectManager: HealthConnectManager) :
    ViewModel() {

    val permissions = setOf(

        /*
        * ExerciseSessionRecord
        * */
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        /*
        * SleepSessionRecord
        * */
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class)
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
                sessionsList.value = sessionsList.value + session
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

class SleepSessionViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SleepSessionViewModel(
                healthConnectManager = healthConnectManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
