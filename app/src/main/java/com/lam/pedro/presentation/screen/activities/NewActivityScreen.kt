package com.lam.pedro.presentation.screen.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.data.activitySession.RunSession
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.DeniedPermissionDialog
import com.lam.pedro.presentation.component.NewActivityControlButtons
import com.lam.pedro.presentation.component.NewActivitySaveAlertDialog
import com.lam.pedro.presentation.component.NewActivityTopAppBar
import com.lam.pedro.presentation.component.PlayPauseButton
import com.lam.pedro.presentation.component.StatsRow
import com.lam.pedro.presentation.component.StopButton
import com.lam.pedro.presentation.component.TimerStatsDisplay
import com.lam.pedro.presentation.navigation.Screen
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
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewActivityScreen(
    navController: NavController,
    titleId: Int,
    color: Color,
    viewModel: ActivitySessionViewModel,
    activityType: Int,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current))
) {
    // variables
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionJob = remember { Job() }
    val sessionScope = remember { CoroutineScope(sessionJob + Dispatchers.Default) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    val stepCounter = remember { StepCounter(context) }
    var steps by remember { mutableFloatStateOf(0f) }
    var averageSpeed by remember { mutableDoubleStateOf(0.0) }
    var speedCounter by remember { mutableIntStateOf(0) }
    var totalSpeed by remember { mutableDoubleStateOf(0.0) }


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
            //showActivityRecognitionPermissionDialog = true
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
            showLocationPermissionDialog = true
        }
    }

    /*---------------------------------------------------------------------------------------------------------*/

    //TODO: coming back from settings you're able to start the activity without permissions
    DeniedPermissionDialog(
        showDialog = showLocationPermissionDialog,
        onDismiss = { showLocationPermissionDialog = false },
        onGoToSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        },
        color = MaterialTheme.colorScheme.primary,
        title = R.string.location_permission_title,
        icon = R.drawable.location_icon,
        text = R.string.location_permission_description
    )

    Scaffold(
        topBar = {
            NewActivityTopAppBar(
                titleId = titleId,
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val density = LocalDensity.current
            var showDialog by remember { mutableStateOf(false) }
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
                TimerStatsDisplay(
                    elapsedTime = elapsedTime,
                    steps = steps,
                    averageSpeed = averageSpeed,
                    distance = distance,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            NewActivityControlButtons(
                isPaused = isPaused,
                visible = visible,
                color = color, // Colore personalizzato
                onPlayPauseClick = {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    if (visible) {
                        isPaused = !isPaused
                    } else {
                        isStopAction = false
                        showDialog = true
                    }
                },
                onStopClick = {
                    isStopAction = true
                    showDialog = true
                },
                density = density
            )


            if (showDialog) {
                NewActivitySaveAlertDialog(
                    showDialog = showDialog,
                    onDismissRequest = { showDialog = false },
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
                                    // Logica per interrompere l'attività
                                    stopActivity(
                                        timerRunning = mutableStateOf(timerRunning),
                                        visible = mutableStateOf(visible),
                                        isPaused = mutableStateOf(isPaused),
                                        elapsedTime = elapsedTime,
                                        timerResults = timerResults,
                                        startTime = startTime,
                                        activityTitle = activityTitle,
                                        notes = notes,
                                        speedSamples = speedSamples,
                                        steps = steps,
                                        profileViewModel = profileViewModel,
                                        distance = mutableDoubleStateOf(distance.value),
                                        exerciseRoute = exerciseRoute,
                                        titleId = titleId,
                                        viewModel = viewModel,
                                        sessionJob = sessionJob,
                                        navController = navController,
                                        activityType = activityType
                                    )
                                } else {
                                    visible = !visible
                                    if (visible) {
                                        isPaused = false
                                        timerRunning = true
                                    }
                                }
                                showDialog = false
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