package com.lam.pedro.presentation.screen.activities

import android.os.RemoteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
//import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.SessionState
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.ActivitySessionFactory
import com.lam.pedro.data.activitySession.CycleSession
import com.lam.pedro.data.activitySession.DriveSession
import com.lam.pedro.data.activitySession.LiftSession
import com.lam.pedro.data.activitySession.ListenSession
import com.lam.pedro.data.activitySession.RunSession
import com.lam.pedro.data.activitySession.SitSession
import com.lam.pedro.data.activitySession.SleepSession
import com.lam.pedro.data.activitySession.TrainSession
import com.lam.pedro.data.activitySession.WalkSession
import com.lam.pedro.data.activitySession.YogaSession
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
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random


abstract class ActivitySessionViewModel(private val healthConnectManager: HealthConnectManager) :
    ViewModel() {

    private val healthConnectCompatibleApps = healthConnectManager.healthConnectCompatibleApps

    /*Define here the required permissions for the Health Connect usage*/
    abstract val permissions: Set<String>

    var permissionsGranted = mutableStateOf(false)
        private set

    var sessionsList: MutableState<List<ActivitySession>> = mutableStateOf(listOf())
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

    suspend fun saveCycleSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Cycle #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        cyclingPedalingCadenceSamples: List<CyclingPedalingCadenceRecord.Sample>,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
         */
        cycleSession: CycleSession
    ) {
        healthConnectManager.insertCycleSession(
            cycleSession.startTime,
            cycleSession.endTime,
            cycleSession.title,
            cycleSession.notes,
            cycleSession.speedSamples,
            cycleSession.cyclingPedalingCadenceSamples,
            cycleSession.totalEnergy,
            cycleSession.activeEnergy,
            cycleSession.distance,
            cycleSession.elevationGained,
            cycleSession.exerciseRoute
        )
    }

    suspend fun saveRunSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        //stepsCadenceSamples: List<StepsCadenceRecord.Sample>,
        stepsCount: Long,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
         */
        runSession: RunSession
    ) {
        sessionsList.value += runSession
        /*
        healthConnectManager.insertRunSession(
            runSession.startTime,
            runSession.endTime,
            runSession.title,
            runSession.notes,
            runSession.speedSamples,
            //stepsCadenceSamples,
            runSession.stepsCount,
            runSession.totalEnergy,
            runSession.activeEnergy,
            runSession.distance,
            runSession.elevationGained,
            runSession.exerciseRoute
        )

         */
    }

    suspend fun saveTrainSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Train #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
         */
        trainSession: TrainSession
    ) {
        healthConnectManager.insertTrainSession(
            trainSession.startTime,
            trainSession.endTime,
            trainSession.title,
            trainSession.notes,
            trainSession.totalEnergy,
            trainSession.activeEnergy,
            trainSession.exerciseSegment,
            trainSession.exerciseLap
        )
    }

    suspend fun saveWalkSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Walk #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        stepsCadenceSamples: List<StepsCadenceRecord.Sample>,
        stepsCount: Long,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
         */
        walkSession: WalkSession
    ) {
        healthConnectManager.insertWalkSession(
            walkSession.startTime,
            walkSession.endTime,
            walkSession.title,
            walkSession.notes,
            walkSession.speedSamples,
            walkSession.stepsCadenceSamples,
            walkSession.stepsCount,
            walkSession.totalEnergy,
            walkSession.activeEnergy,
            walkSession.distance,
            walkSession.elevationGained,
            walkSession.exerciseRoute
        )
    }

    suspend fun saveYogaSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Yoga #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
         */
        yogaSession: YogaSession
    ) {
        healthConnectManager.insertYogaSession(
            yogaSession.startTime,
            yogaSession.endTime,
            yogaSession.title,
            yogaSession.notes,
            yogaSession.totalEnergy,
            yogaSession.activeEnergy,
            yogaSession.exerciseSegment,
            yogaSession.exerciseLap
        )
    }

    suspend fun saveDriveSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Drive #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
         */
        driveSession: DriveSession
    ) {
        healthConnectManager.insertDriveSession(
            driveSession.startTime,
            driveSession.endTime,
            driveSession.title,
            driveSession.notes,
            driveSession.speedSamples,
            driveSession.distance,
            driveSession.elevationGained,
            driveSession.exerciseRoute
        )
    }

    suspend fun saveLiftSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Lift #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
         */
        liftSession: LiftSession
    ) {
        healthConnectManager.insertLiftSession(
            liftSession.startTime,
            liftSession.endTime,
            liftSession.title,
            liftSession.notes,
            liftSession.totalEnergy,
            liftSession.activeEnergy,
            liftSession.exerciseSegment,
            liftSession.exerciseLap
        )
    }

    suspend fun saveListenSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Listen #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String
         */
        listenSession: ListenSession
    ) {
        healthConnectManager.insertListenSession(
            listenSession.startTime,
            listenSession.endTime,
            listenSession.title,
            listenSession.notes
        )
    }

    suspend fun saveSitSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Sit #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        volume: Volume
         */
        sitSession: SitSession
    ) {
        healthConnectManager.insertSitSession(
            sitSession.startTime,
            sitSession.endTime,
            sitSession.title,
            sitSession.notes,
            sitSession.volume
        )
    }

    suspend fun saveSleepSession(
        /*
        startTime: Instant,
        endTime: Instant,
        title: String = "My Sleep #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String
         */
        sleepSession: SleepSession
    ) {
        healthConnectManager.insertSleepSession(
            sleepSession.startTime,
            sleepSession.endTime,
            sleepSession.title,
            sleepSession.notes
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
                healthConnectManager.insertExerciseSession(
                    startOfSession.toInstant(), endOfSession.toInstant(), exerciseType, title, notes
                )
                //fetchExerciseSessions(exerciseType)  // aggiorna la lista delle sessioni
            }
        }
    }

    suspend fun fetchExerciseSessions(exerciseType: Int) {
        val start = Instant.EPOCH // 1st January 1970
        val now = Instant.now()

        // Chiamata al metodo del manager per leggere i dati da Health Connect
        val records = healthConnectManager.readExerciseSessions(start, now, exerciseType)

        /*
        sessionsList.value = records.map { record ->
            ActivitySessionFactory.create(exerciseType, record)
        }

         */
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

