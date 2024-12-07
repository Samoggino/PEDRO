package com.lam.pedro.presentation.screen.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.data.activitySession.RunSession
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.DisplayLottieAnimation
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModelFactory
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
import com.lam.pedro.util.calculateTotalDistance
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionJob = remember { Job() }
    val sessionScope = remember { CoroutineScope(sessionJob + Dispatchers.Default) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocationPermissionDialog by remember { mutableStateOf(false) }

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

    //TODO: coming back from settings you're able to start the activity without permissions
    if (showLocationPermissionDialog) {
        Dialog(onDismissRequest = { showLocationPermissionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Location necessary",
                        style = MaterialTheme.typography.headlineMedium,
                        color = color
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.location_icon),
                        contentDescription = "Location icon",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.location_permission_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        showLocationPermissionDialog = false
                        //requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    }) {
                        Text("Go to settings")
                    }
                }
            }
        }
    }

    val stepCounter = remember { StepCounter(context) }

    var steps by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(titleId) + " - New activity",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        BackButton(navController)
                    }
                }
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

            var speedSamples = remember { mutableStateListOf<SpeedRecord.Sample>() }
            var exerciseRoute = remember { mutableStateListOf<ExerciseRoute.Location>() }

            Spacer(modifier = Modifier.height(60.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .size(150.dp)
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
                            modifier = Modifier.size(75.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally { with(density) { -40.dp.roundToPx() } } + fadeIn(),
                    exit = slideOutHorizontally { with(density) { -40.dp.roundToPx() } } + fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            isStopAction = true
                            showDialog = true
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(26.dp))
                            .size(150.dp)
                            .background(Color(0xFFF44336))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.stop_icon),
                            contentDescription = "Stop",
                            modifier = Modifier.size(75.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            var title by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            var isTitleEmpty by remember { mutableStateOf(false) }

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
                                value = title,
                                onValueChange = {
                                    title = it
                                    isTitleEmpty = title.isBlank()
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

                                if (title.isNotBlank()) {
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
                                                val positions = exerciseRoute.map {
                                                    LatLng(
                                                        it.latitude,
                                                        it.longitude
                                                    )
                                                }
                                                val distance = calculateTotalDistance(positions)
                                                val runSession = RunSession(
                                                    startTime = startTime.toInstant(),
                                                    endTime = endTime.toInstant(),
                                                    title = title,
                                                    notes = notes,
                                                    speedSamples = speedSamples,
                                                    stepsCount = steps.toLong(),
                                                    totalEnergy = Energy.calories(profileViewModel.weight.toDouble()),
                                                    activeEnergy = Energy.calories(3.0),
                                                    distance = Length.meters(distance),
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
                            val stepCount = stepCounter.steps() // Chiama la funzione suspend
                            Log.d("STEP_COUNTER", "Steps: $stepCount")
                            steps = stepCount.toFloat() // Aggiorna lo stato
                        } catch (e: Exception) {
                            Log.e("STEP_COUNTER", "Error retrieving steps: ${e.message}")
                        }
                    }

                    sessionScope.launch {
                        speedTracker.trackSpeed().collect { sample ->
                            speedSamples.add(sample)
                            Log.d(TAG, "----------------------New Speed Sample: $sample")
                        }
                    }

                    sessionScope.launch {
                        locationTracker.trackLocation().collect { location ->
                            exerciseRoute.add(location)
                            Log.d(TAG, "--------------------------------New location: $location")
                        }
                    }
                }
            }

            AnimatedVisibility(visible = timerRunning || !isPaused) {
                val minutes = (elapsedTime / 60000) % 60
                val seconds = (elapsedTime / 1000) % 60
                val centiseconds = (elapsedTime % 1000) / 10

                Column(
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

                    Spacer(modifier = Modifier.height(60.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (titleId == Screen.RunSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/d32ef6d1-6bd0-4e39-b2f4-cbab8ca8c19d/79Mbx9ocLg.lottie")
                        } else if (titleId == Screen.CycleSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/58060237-49bc-4e38-b630-9db0992858e3/QkvECf9V38.lottie")
                        } else if (titleId == Screen.TrainSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/80db1f9c-c1f6-4f2d-8512-fbbee80d23d0/DeQ19gEueZ.lottie")
                        } else if (titleId == Screen.WalkSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/d32ef6d1-6bd0-4e39-b2f4-cbab8ca8c19d/79Mbx9ocLg.lottie")
                        } else if (titleId == Screen.YogaSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/d32ef6d1-6bd0-4e39-b2f4-cbab8ca8c19d/79Mbx9ocLg.lottie")
                        } else if (titleId == Screen.DriveSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/bb545e90-529a-4bfd-aff9-1324080aaa4b/HF3zQgFQFe.lottie")
                        } else if (titleId == Screen.WeightScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/bb545e90-529a-4bfd-aff9-1324080aaa4b/HF3zQgFQFe.lottie")
                        } else if (titleId == Screen.ListenSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/bb545e90-529a-4bfd-aff9-1324080aaa4b/HF3zQgFQFe.lottie")
                        } else if (titleId == Screen.SitSessionScreen.titleId) {
                            DisplayLottieAnimation("https://lottie.host/bb545e90-529a-4bfd-aff9-1324080aaa4b/HF3zQgFQFe.lottie")
                        } else if (titleId == Screen.SleepSessions.titleId) {
                            DisplayLottieAnimation("https://lottie.host/bb545e90-529a-4bfd-aff9-1324080aaa4b/HF3zQgFQFe.lottie")
                        }
                    }
                }
            }
        }
    }
}