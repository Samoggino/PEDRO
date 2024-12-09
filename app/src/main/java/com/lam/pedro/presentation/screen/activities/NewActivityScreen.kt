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
import com.lam.pedro.presentation.component.NewActivityTopAppBar
import com.lam.pedro.presentation.component.StatsRow
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModelFactory
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
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
            var endTime: ZonedDateTime
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
                val minutes = (elapsedTime / 60000) % 60
                val seconds = (elapsedTime / 1000) % 60
                val centiseconds = (elapsedTime % 1000) / 10

                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        String.format("%02d:%02d:%02d", minutes, seconds, centiseconds),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    StatsRow(
                        steps = steps,
                        speed = averageSpeed,
                        distance = distance,
                        color = color,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .size(140.dp)
                        .background(color)
                ) {
                    IconButton(
                        onClick = {
                            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            if (visible) {
                                isPaused = !isPaused
                            } else {
                                isStopAction = false
                                showDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = if (!isPaused) R.drawable.pause_icon else R.drawable.play_icon),
                            contentDescription = if (visible) "Pause" else "Play",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally { with(density) { -40.dp.roundToPx() } } + fadeIn(),
                    exit = slideOutHorizontally { with(density) { -40.dp.roundToPx() } } + fadeOut()
                ) {
                    Row() {
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButton(
                            onClick = {
                                isStopAction = true
                                showDialog = true
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(26.dp))
                                .size(140.dp)
                                .background(Color(0xFFF44336))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.stop_icon),
                                contentDescription = "Stop",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Confirm", color = color) },
                    text = {
                        Column {
                            Text(
                                if (isStopAction) "Want to stop the activity? (you can change these while stopping)" else if (visible) "Want to pause?" else "Want to start?"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = activityTitle,
                                onValueChange = {
                                    activityTitle = it
                                    isTitleEmpty = activityTitle.isBlank()
                                },
                                label = { Text("Titolo") },
                                isError = isTitleEmpty,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(26.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = color,
                                    cursorColor = color,
                                    focusedLabelColor = color,
                                )
                            )

                            if (isTitleEmpty) {
                                Text(
                                    text = "Title is required",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Note (optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(26.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = color,
                                    cursorColor = color,
                                    focusedLabelColor = color,
                                )
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {

                                if (activityTitle.isNotBlank()) {
                                    coroutineScope.launch {
                                        if (isStopAction) {
                                            timerRunning = false
                                            visible = false
                                            isPaused = true

                                            val minutes = (elapsedTime / 60000) % 60
                                            val seconds = (elapsedTime / 1000) % 60
                                            val centiseconds = (elapsedTime % 1000) / 10
                                            timerResults.add(
                                                String.format(
                                                    "%02d:%02d:%02d",
                                                    minutes,
                                                    seconds,
                                                    centiseconds
                                                )
                                            )
                                            Log.d(TAG, "------------Timer results: $timerResults")

                                            endTime = ZonedDateTime.now()

                                            if (titleId == Screen.RunSessionScreen.titleId) {
                                                val runSession = RunSession(
                                                    startTime = startTime.toInstant(),
                                                    endTime = endTime.toInstant(),
                                                    title = activityTitle,
                                                    notes = notes,
                                                    speedSamples = speedSamples,
                                                    stepsCount = steps.toLong(),
                                                    totalEnergy = Energy.calories(profileViewModel.weight.toDouble()),
                                                    activeEnergy = Energy.calories(3.0),
                                                    distance = Length.meters(distance.value),
                                                    elevationGained = Length.meters(3.0),
                                                    exerciseRoute = ExerciseRoute(exerciseRoute)
                                                )
                                                Log.d(TAG, "------------Run session: $runSession")
                                                viewModel.saveRunSession(runSession)

                                                sessionJob.cancelAndJoin()
                                            }

                                            viewModel.fetchExerciseSessions(activityType)

                                            elapsedTime = 0

                                            navController.popBackStack()
                                        } else {
                                            visible = !visible

                                            if (visible) {
                                                isPaused = false
                                                timerRunning = true
                                            }
                                        }
                                        showDialog = false
                                    }
                                } else {
                                    isTitleEmpty = true
                                }
                            }
                        ) {
                            Text(text = "Yes", color = color)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = "Dismiss", color = color)
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