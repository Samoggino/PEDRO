package com.lam.pedro.presentation.screen.runreadings

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.units.Length
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.RunData
import com.lam.pedro.data.StepsData
import com.lam.pedro.data.dateTimeWithOffsetOrDefault
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class RunViewModel(private val healthConnectManager: HealthConnectManager) : ViewModel() {
    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    val permissions = setOf(
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
    )

    var weeklyAvgDistance: MutableState<Length?> = mutableStateOf(Length.meters(0.0))
        private set

    var weeklyAvgSteps: MutableState<Long?> = mutableStateOf(0L)
        private set

    var permissionsGranted = mutableStateOf(false)
        private set

    var runReadingsList: MutableState<List<RunData>> = mutableStateOf(listOf())
        private set

    var stepsList: MutableState<List<StepsData>> = mutableStateOf(listOf())
        private set

    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    val permissionsLauncher = healthConnectManager.requestPermissionsActivityContract()

    fun initialLoad() {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                readRunInputs()
                readStepInputs()
            }
        }
    }

    fun inputRun(distance: Double, durationSeconds: Long) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val startTime = ZonedDateTime.now().withNano(0)
                val endTime = startTime.plusSeconds(durationSeconds)
                val distanceRecord = DistanceRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    distance = Length.meters(distance),
                    //metadata = healthConnectManager.createMetadata() // Assicurati di avere un metodo per creare metadata
                )
                healthConnectManager.writeRunInput(distanceRecord)
                readRunInputs()
            }
        }
    }

    fun inputSteps(stepCount: Long, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                val time = ZonedDateTime.now().withNano(0)
                val stepsRecord = StepsRecord(
                    startTime = startTime.toInstant(),
                    startZoneOffset = startTime.offset,
                    endTime = endTime.toInstant(),
                    endZoneOffset = endTime.offset,
                    count = stepCount,
                )
                healthConnectManager.writeStepsInput(stepsRecord)
                readStepInputs()
            }
        }
    }


    fun deleteRunInput(uid: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthConnectManager.deleteRunInput(uid)
                readRunInputs()
            }
        }
    }

    fun deleteStepInput(uid: String) {
        viewModelScope.launch {
            tryWithPermissionsCheck {
                healthConnectManager.deleteStepInput(uid)
                readStepInputs()
            }
        }
    }

    private suspend fun readRunInputs() {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endOfWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)

        runReadingsList.value = healthConnectManager
            .readRunInputs(startOfDay.toInstant(), now)
            .map { record ->
                val packageName = record.metadata.dataOrigin.packageName
                RunData(
                    distance = record.distance,
                    duration = record.endTime.epochSecond - record.startTime.epochSecond, // Calcola la durata
                    id = record.metadata.id,
                    time = dateTimeWithOffsetOrDefault(record.startTime, record.startZoneOffset),
                    sourceAppInfo = healthConnectCompatibleApps[packageName]
                )
            }
        weeklyAvgDistance.value =
            healthConnectManager.computeWeeklyDistanceAverage(startOfDay.toInstant(), endOfWeek)
    }

    private suspend fun readStepInputs() {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val now = Instant.now()
        val endOfWeek = startOfDay.toInstant().plus(7, ChronoUnit.DAYS)

        stepsList.value = healthConnectManager
            .readStepInputs(startOfDay.toInstant(), now)
            .map { record ->
                val packageName = record.metadata.dataOrigin.packageName
                StepsData(
                    stepCount = record.count,
                    id = record.metadata.id,
                    time = dateTimeWithOffsetOrDefault(record.time, record.zoneOffset),
                    sourceAppInfo = healthConnectCompatibleApps[packageName]
                )
            }
        weeklyAvgSteps.value =
            healthConnectManager.computeWeeklyStepsAverage(startOfDay.toInstant(), endOfWeek)
    }

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
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

class RunViewModelFactory(
    private val healthConnectManager: HealthConnectManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RunViewModel(healthConnectManager = healthConnectManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
