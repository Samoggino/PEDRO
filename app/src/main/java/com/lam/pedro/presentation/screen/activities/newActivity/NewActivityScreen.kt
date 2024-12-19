package com.lam.pedro.presentation.screen.activities.newActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.presentation.component.DeniedPermissionDialog
import com.lam.pedro.presentation.component.NewActivityControlButtons
import com.lam.pedro.presentation.component.NewActivitySaveAlertDialog
import com.lam.pedro.presentation.component.NewActivityTopAppBar
import com.lam.pedro.presentation.component.StatsDisplay
import com.lam.pedro.presentation.component.TimerDisplay
import com.lam.pedro.presentation.component.TrainIntensitySelector
import com.lam.pedro.presentation.component.WaterGlass
import com.lam.pedro.presentation.component.YogaStyleSelector
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.GpsFunctionality
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenContext
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.StepCounterFunctionality
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModelFactory
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
import com.lam.pedro.util.stopActivity
import com.lam.pedro.util.updateDistance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun NewActivityScreen(
    navController: NavController,
    titleId: Int,
    color: Color,
    viewModel: ActivitySessionViewModel,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current))
) {

    Log.d("TIPO DEL NEW ACTIVITY SCREEN", "---- TIPO DEL NEW ACTIVITY SCREEN: ${viewModel.activityType}")
    // variables
    val activityType = viewModel.activityType
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionJob = remember { Job() }
    val sessionScope = remember { CoroutineScope(sessionJob + Dispatchers.Default) }
    val stepCounter = remember { StepCounter(context) }
    var steps by remember { mutableFloatStateOf(0f) }
    var hydrationVolume by remember { mutableDoubleStateOf(0.0) }
    var yogaStyle by remember { mutableStateOf("Yin (gentle)") }
    var trainIntensity by remember { mutableStateOf("moderate") }
    var averageSpeed by remember { mutableDoubleStateOf(0.0) }
    var speedCounter by remember { mutableIntStateOf(0) }
    var totalSpeed by remember { mutableDoubleStateOf(0.0) }

    /*
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    var showActivityRecognitionPermissionDialog by remember { mutableStateOf(false) }
    var requestLocationPermissionCounter by remember { mutableIntStateOf(0) }
    var hasBeenAskedForLocationPermission by remember { mutableStateOf(false) }
    var hasBeenAskedForActivityRecognitionPermission by remember { mutableStateOf(false) }
     */

    val lifecycleOwner = LocalLifecycleOwner.current

    val snackbarHostState = remember { SnackbarHostState() }

/*
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Stato per il permesso di ACTIVITY_RECOGNITION
    var hasActivityRecognitionPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher per richiedere il permesso di ACTIVITY_RECOGNITION
    val requestActivityRecognitionPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasActivityRecognitionPermission = isGranted
        if (isGranted) {
            Log.d(TAG, "-----------------Activity Recognition Permission granted-----------------")
        } else {
            //TODO: Handle permission denied, inform the user
            Log.d(TAG, "-----------------Activity Recognition Permission denied-----------------")
            hasBeenAskedForActivityRecognitionPermission = true
            showActivityRecognitionPermissionDialog = true
        }
    }

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            Log.d(TAG, "-----------------GPS Permission granted-----------------")
            requestActivityRecognitionPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            //TODO: Handle permission denied, the app won't work
            Log.d(TAG, "-----------------GPS Permission denied-----------------")
            hasBeenAskedForLocationPermission = true
            requestLocationPermissionCounter++
            showLocationPermissionDialog = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Controlla se i permessi sono concessi quando lo screen torna attivo
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //if (!isFirstTimeRequest) {
                        hasLocationPermission = true
                        showLocationPermissionDialog = false
                        requestActivityRecognitionPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        //}
                    } else {
                        if (hasBeenAskedForLocationPermission)
                            showLocationPermissionDialog = true
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Controlla se il permesso di ACTIVITY_RECOGNITION è stato concesso
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACTIVITY_RECOGNITION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        hasActivityRecognitionPermission = true
                        showActivityRecognitionPermissionDialog = false
                    } else {
                        // Mostra il dialog se il permesso non è stato concesso
                        if (hasBeenAskedForActivityRecognitionPermission)
                            showActivityRecognitionPermissionDialog = true
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DeniedPermissionDialog(
        showDialog = showLocationPermissionDialog,
        onDismiss = {
            if (hasLocationPermission) {
                showLocationPermissionDialog = false
            }
        },
        onGoToSettings = {
            if (requestLocationPermissionCounter < 2) {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        },
        color = MaterialTheme.colorScheme.primary,
        title = R.string.location_permission_title,
        icon = R.drawable.location_icon,
        text = R.string.location_permission_description,
        buttonText = if (requestLocationPermissionCounter < 2) R.string.request_permission else R.string.go_to_settings
    )

    DeniedPermissionDialog(
        showDialog = showActivityRecognitionPermissionDialog,
        onDismiss = {
            if (hasActivityRecognitionPermission) {
                showActivityRecognitionPermissionDialog = false
            }
        },
        onGoToSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        },
        color = MaterialTheme.colorScheme.primary,
        title = R.string.activity_recognition_permission_title,
        icon = R.drawable.steps_icon,
        text = R.string.activity_recognition_permission_description,
        buttonText = R.string.go_to_settings
    )
     */


    /*------------------------------------------------------------------------------------------------------------------*/

    // Crea la lista di funzionalità
    val functionalities = listOf(GpsFunctionality(context), StepCounterFunctionality(context))

    // Crea il contesto con le funzionalità
    val screenContext = remember { ScreenContext(functionalities) }

    // Esegui tutte le funzionalità
    screenContext.ExecuteFunctionalities()

    Scaffold(
        topBar = {
            NewActivityTopAppBar(
                titleId = titleId,
                navController = navController
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val density = LocalDensity.current
            var showConfirmDialog by remember { mutableStateOf(false) }
            var isStopAction by remember { mutableStateOf(false) }
            var visible by remember { mutableStateOf(false) }
            var isPaused by remember { mutableStateOf(true) }

            var timerRunning by remember { mutableStateOf(false) }
            var elapsedTime by remember { mutableIntStateOf(0) }

            var startTime: ZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }
            val speedTracker = SpeedTracker(LocalContext.current)
            val locationTracker = LocationTracker(LocalContext.current)

            val timerResults = remember { mutableStateListOf<String>() }

            val speedSamples = remember { mutableStateListOf<SpeedRecord.Sample>() }
            val exerciseRoute = remember { mutableStateListOf<ExerciseRoute.Location>() }

            val positions = remember { mutableStateListOf<LatLng>() }
            val distance = remember { mutableDoubleStateOf(0.0) }

            var activityTitle by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            var isTitleEmpty by remember { mutableStateOf(false) }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (activityTitle == "") "Press to start" else activityTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 40.sp),
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(visible = timerRunning || !isPaused) {

                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TimerDisplay(
                        elapsedTime = elapsedTime,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    when (activityType) {
                        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING, ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> {
                            StatsDisplay(
                                steps = steps,
                                averageSpeed = averageSpeed,
                                distance = distance,
                                color = color
                            )
                        }
                        ExerciseSessionRecord.EXERCISE_TYPE_BIKING, ExerciseSessionRecord.EXERCISE_TYPE_SURFING -> {
                            StatsDisplay(
                                averageSpeed = averageSpeed,
                                distance = distance,
                                color = color
                            )
                        }
                        ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> {
                            YogaStyleSelector(
                                yogaStyle = yogaStyle,
                                onYogaStyleChange = { newStyle -> yogaStyle = newStyle },
                                color = color
                            )
                        }
                        ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS -> {
                            TrainIntensitySelector(
                                trainIntensity = trainIntensity,
                                onTrainIntensityChange = { newStyle -> trainIntensity = newStyle },
                                color = color
                            )
                        }
                        ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR -> {
                            WaterGlass(hydrationVolume = hydrationVolume) { addedVolume ->
                                hydrationVolume += addedVolume
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            NewActivityControlButtons(
                isPaused = isPaused,
                visible = visible,
                color = color, // Colore personalizzato
                onPlayPauseClick = {
                    //requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (visible) {
                        isPaused = !isPaused
                    } else {
                        isStopAction = false
                        showConfirmDialog = true
                    }
                },
                onStopClick = {
                    isStopAction = true
                    showConfirmDialog = true
                },
                density = density
            )

            if (showConfirmDialog) {
                NewActivitySaveAlertDialog(
                    showDialog = showConfirmDialog,
                    onDismissRequest = { showConfirmDialog = false },
                    isStopAction = isStopAction,
                    visible = visible,
                    color = color,
                    activityTitle = activityTitle,
                    onTitleChange = {
                        activityTitle = it
                        isTitleEmpty = activityTitle.isBlank()
                    },
                    isTitleEmpty = isTitleEmpty,
                    notes = notes,
                    onNotesChange = { notes = it },
                    onConfirm = {
                        coroutineScope.launch {
                            if (activityTitle.isNotBlank()) {
                                if (isStopAction) {
                                    val endTime = ZonedDateTime.now()
                                    val duration = Duration.between(startTime, endTime).toMinutes()
                                    if (duration < 1) {
                                        showConfirmDialog = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Activity must be at least 1 minute",
                                                actionLabel = "OK",
                                                duration = SnackbarDuration.Long // Short, Long o Indefinite
                                            )
                                        }
                                    } else {
                                        stopActivity(
                                            timerRunning = mutableStateOf(timerRunning),
                                            visible = mutableStateOf(visible),
                                            isPaused = mutableStateOf(isPaused),
                                            elapsedTime = elapsedTime,
                                            timerResults = timerResults,
                                            duration = duration,
                                            startTime = startTime,
                                            endTime = endTime,
                                            activityTitle = activityTitle,
                                            notes = notes,
                                            speedSamples = speedSamples,
                                            steps = steps,
                                            hydrationVolume = hydrationVolume,
                                            trainIntensity = trainIntensity,
                                            yogaStyle = yogaStyle,
                                            profileViewModel = profileViewModel,
                                            distance = mutableDoubleStateOf(distance.doubleValue),
                                            exerciseRoute = exerciseRoute,
                                            viewModel = viewModel
                                        )
                                    }
                                    //coroutineScope.launch {
                                    sessionJob.cancelAndJoin()  // Cancella il sessionJob in background
                                    //}
                                    navController.popBackStack()

                                } else {
                                    visible = !visible
                                    if (visible) {
                                        isPaused = false
                                        timerRunning = true
                                    }
                                }
                                showConfirmDialog = false
                            } else {
                                isTitleEmpty = true
                            }
                        }
                    }
                )
            }

            if (timerRunning && !isPaused) {
                LaunchedEffect(Unit) {
                    startTime = ZonedDateTime.now()

                    sessionScope.launch {
                        while (timerRunning) {
                            delay(10)
                            elapsedTime += 10
                        }
                    }

                    sessionScope.launch {
                        try {
                            stepCounter.isAvailable()
                            stepCounter.stepsCounter { newSteps ->
                                steps = newSteps // Aggiorna lo stato della UI
                            }
                            //steps = stepCount.toFloat() // Aggiorna lo stato
                            Log.d("STEP_COUNTER", "Steps: $steps")
                        } catch (e: Exception) {
                            Log.e("STEP_COUNTER", "Error retrieving steps: ${e.message}")
                        }
                    }

                    sessionScope.launch {
                        speedTracker.trackSpeed().collect { sample ->
                            speedCounter++
                            totalSpeed += sample.speed.inMetersPerSecond
                            averageSpeed = totalSpeed / speedCounter
                            speedSamples.add(sample)
                            Log.d(TAG, "----------------------New Speed Sample: $sample")
                        }
                    }

                    sessionScope.launch {
                        locationTracker.trackLocation().collect { location ->
                            exerciseRoute.add(location)
                            Log.d(TAG, "--------------------------------New location: $location")
                            val newLatLng = LatLng(location.latitude, location.longitude)
                            updateDistance(distance, positions, newLatLng)
                            positions.add(newLatLng)
                            Log.d(
                                TAG,
                                "--------------------------------New distance: ${distance.value}"
                            )
                        }
                    }
                }
            }

        }
    }


}