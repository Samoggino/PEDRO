package com.lam.pedro.presentation.screen.activities.newActivity

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.exerciseRoute
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.speedSamples
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.GpsFunctionality
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenContext
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.StepCounterFunctionality
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
import com.lam.pedro.util.stopActivity
import org.maplibre.android.geometry.LatLng
import java.time.ZonedDateTime

class NewActivityViewModel(context: Context, repository: ActivityTrackingRepository) :
    ViewModel() {

    private val stepCounter = StepCounter(context)
    //var steps by remember { mutableFloatStateOf(0f) }
    //var steps = mutableFloatStateOf(0f)
    //var averageSpeed = mutableDoubleStateOf(0.0)
    //val distance = mutableDoubleStateOf(0.0)

    val steps = repository.steps

    val averageSpeed = repository.averageSpeed

    val distance = repository.distance

    var hydrationVolume = mutableDoubleStateOf(0.0)
    var yogaStyle = mutableStateOf("Yin (gentle)")
    var trainIntensity = mutableStateOf("moderate")

    //var averageSpeed by remember { mutableDoubleStateOf(0.0) }
    var speedCounter = mutableIntStateOf(0)
    var totalSpeed = mutableDoubleStateOf(0.0)

    val snackbarHostState = SnackbarHostState()

    val speedTracker = SpeedTracker(context)
    val locationTracker = LocationTracker(context)

    //val speedSamples = mutableStateListOf<SpeedRecord.Sample>()
    //val exerciseRoute = mutableStateListOf<ExerciseRoute.Location>()

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