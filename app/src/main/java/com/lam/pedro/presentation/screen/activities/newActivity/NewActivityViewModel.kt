package com.lam.pedro.presentation.screen.activities.newActivity

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.lifecycle.ViewModel
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.GpsFunctionality
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenContext
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.StepCounterFunctionality
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
import com.lam.pedro.util.stopActivity
import com.lam.pedro.util.updateDistance
import org.maplibre.android.geometry.LatLng
import java.time.ZonedDateTime

class NewActivityViewModel(context: Context) : ViewModel() {

    private val stepCounter = StepCounter(context)
    //var steps by remember { mutableFloatStateOf(0f) }
    var steps = mutableFloatStateOf(0f)
    var averageSpeed = mutableDoubleStateOf(0.0)
    val distance = mutableDoubleStateOf(0.0)

    var hydrationVolume = mutableDoubleStateOf(0.0)
    var yogaStyle = mutableStateOf("Yin (gentle)")
    var trainIntensity = mutableStateOf("moderate")
    //var averageSpeed by remember { mutableDoubleStateOf(0.0) }
    var speedCounter = mutableIntStateOf(0)
    var totalSpeed = mutableDoubleStateOf(0.0)

    val snackbarHostState = SnackbarHostState()

    val speedTracker = SpeedTracker(context)
    val locationTracker = LocationTracker(context)

    val speedSamples = mutableStateListOf<SpeedRecord.Sample>()
    val exerciseRoute = mutableStateListOf<ExerciseRoute.Location>()

    val positions = mutableStateListOf<LatLng>()

    var activityTitle = mutableStateOf("")
    var notes = mutableStateOf("")
    var isTitleEmpty = mutableStateOf(false)

    // Crea la lista di funzionalità
    private val functionalities = listOf(GpsFunctionality(context), StepCounterFunctionality(context))

    // Crea il contesto con le funzionalità
    private val screenContext = ScreenContext(functionalities)

    @Composable
    fun ExecuteFunctionalities() {
        screenContext.ExecuteFunctionalities()
    }

    fun updateTitle(newTitle: String) {
        activityTitle.value = newTitle
    }

    suspend fun startSpeedTracking() {
        speedTracker.trackSpeed().collect { sample ->
            speedCounter.intValue++
            totalSpeed.value += sample.speed.inMetersPerSecond
            averageSpeed.doubleValue =
                totalSpeed.doubleValue / speedCounter.intValue
            speedSamples.add(sample)
            Log.d(TAG, "----------------------New Speed Sample: $sample")
        }
    }

    suspend fun startLocationTracking() {
        locationTracker.trackLocation().collect { location ->
            exerciseRoute.add(location)
            Log.d(TAG, "--------------------------------New location: $location")
            val newLatLng = LatLng(location.latitude, location.longitude)
            updateDistance(distance, positions, newLatLng)
            positions.add(newLatLng)
            Log.d(
                TAG,
                "--------------------------------New distance: ${distance.doubleValue}"
            )
        }
    }

    suspend fun startStepCounter() {
        try {
            stepCounter.isAvailable()
            stepCounter.stepsCounter { newSteps ->
                steps.floatValue = newSteps // Aggiorna lo stato della UI
            }
            //steps = stepCount.toFloat() // Aggiorna lo stato
            Log.d("STEP_COUNTER", "Steps: $steps")
        } catch (e: Exception) {
            Log.e("STEP_COUNTER", "Error retrieving steps: ${e.message}")
        }
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
            activityTitle = activityTitle.toString(),
            notes = notes.toString(),
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
