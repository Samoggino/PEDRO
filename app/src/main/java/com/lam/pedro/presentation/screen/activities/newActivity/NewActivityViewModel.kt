package com.lam.pedro.presentation.screen.activities.newActivity

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.exerciseRoute
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.speedSamples
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenContext
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenFunctionality
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
import com.lam.pedro.util.services.ActivityTrackingService
import com.lam.pedro.util.stopActivity
import org.maplibre.android.geometry.LatLng
import java.time.ZonedDateTime

/*
class NewActivityViewModel(context: Context, repository: ActivityTrackingRepository) :
    ViewModel() {

    private val stepCounter = StepCounter(context)

    val steps = repository.steps

    val averageSpeed = repository.averageSpeed

    val distance = repository.distance

    var hydrationVolume = mutableDoubleStateOf(0.0)
    var yogaStyle = mutableStateOf("Yin (gentle)")
    var trainIntensity = mutableStateOf("moderate")

    var speedCounter = mutableIntStateOf(0)
    var totalSpeed = mutableDoubleStateOf(0.0)

    val snackbarHostState = SnackbarHostState()

    val speedTracker = SpeedTracker(context)
    val locationTracker = LocationTracker(context)

    val positions = mutableStateListOf<LatLng>()

    var activityTitle = mutableStateOf("")
    var notes = mutableStateOf("")
    var isTitleEmpty = mutableStateOf(false)

    // Crea la lista di funzionalità
    private val functionalities =
        listOf(GpsFunctionality(context), StepCounterFunctionality(context))

    // Crea il contesto con le funzionalità
    private val screenContext = ScreenContext(functionalities)

    @Composable
    fun ExecuteFunctionalities() {
        screenContext.ExecuteFunctionalities()
    }

    fun updateTitle(newTitle: String) {
        activityTitle.value = newTitle
    }

    suspend fun saveActivity(
        elapsedTime: Int,
        timerResults: MutableList<String>,
        duration: Long,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        profileViewModel: ProfileViewModel,
        activitySessionViewModel: ActivitySessionViewModel
    ) {
        stopActivity(
            elapsedTime = elapsedTime,
            timerResults = timerResults,
            duration = duration,
            startTime = startTime,
            endTime = endTime,
            activityTitle = activityTitle.value,
            notes = notes.value,
            speedSamples = speedSamples,
            steps = steps.floatValue,
            hydrationVolume = hydrationVolume.doubleValue,
            trainIntensity = trainIntensity.toString(),
            yogaStyle = yogaStyle.toString(),
            profileViewModel = profileViewModel,
            distance = distance.doubleValue,
            exerciseRoute = exerciseRoute,
            viewModel = activitySessionViewModel
        )
    }

}

 */

abstract class NewActivityViewModel(
    val context: Context,
    private val repository: ActivityTrackingRepository
) : ViewModel() {

    val snackbarHostState = SnackbarHostState()

    // Elementi comuni a tutte le attività
    var activityTitle = mutableStateOf("")
    var notes = mutableStateOf("")
    var isTitleEmpty = mutableStateOf(false)

    // elementi non comuni a tutte le attivita

    val steps = repository.steps
    val averageSpeed = repository.averageSpeed
    val distance = repository.distance
    var hydrationVolume = mutableDoubleStateOf(0.0)
    var yogaStyle = mutableStateOf("Yin (gentle)")
    var trainIntensity = mutableStateOf("moderate")
    /*
    var speedCounter = mutableIntStateOf(0)
    var totalSpeed = mutableDoubleStateOf(0.0)

     */

    /*
    private val stepCounter = StepCounter(context)
    val speedTracker = SpeedTracker(context)
    val locationTracker = LocationTracker(context)
    val positions = mutableStateListOf<LatLng>()

     */

    // Funzionalità specifiche saranno definite nelle sottoclassi
    protected abstract val functionalities: List<ScreenFunctionality>

    // Contesto con funzionalità
    private val screenContext by lazy { ScreenContext(functionalities) }

    // Funzione comune per eseguire funzionalità
    @Composable
    fun ExecuteFunctionalities() {
        if (functionalities.isNotEmpty()) {
            screenContext.ExecuteFunctionalities()
        }
    }

    fun startTrackingServiceIfNeeded(context: Context): Intent? {
        return if (functionalities.isNotEmpty()) {
            Intent(context, ActivityTrackingService::class.java)
        } else {
            null
        }
    }


    // Metodo per aggiornare il titolo
    fun updateTitle(newTitle: String) {
        activityTitle.value = newTitle
    }

    suspend fun saveActivity(
        elapsedTime: Int,
        timerResults: MutableList<String>,
        duration: Long,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        profileViewModel: ProfileViewModel,
        activitySessionViewModel: ActivitySessionViewModel
    ) {
        stopActivity(
            elapsedTime = elapsedTime,
            timerResults = timerResults,
            duration = duration,
            startTime = startTime,
            endTime = endTime,
            activityTitle = activityTitle.value,
            notes = notes.value,
            speedSamples = speedSamples,
            steps = steps.floatValue,
            hydrationVolume = hydrationVolume.doubleValue,
            trainIntensity = trainIntensity.toString(),
            yogaStyle = yogaStyle.toString(),
            profileViewModel = profileViewModel,
            distance = distance.doubleValue,
            exerciseRoute = exerciseRoute,
            viewModel = activitySessionViewModel
        )
    }
}
