package com.lam.pedro.presentation.screen.activities.newActivity

import android.content.Intent
import android.util.Log
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.presentation.component.NewActivityControlButtons
import com.lam.pedro.presentation.component.NewActivitySaveAlertDialog
import com.lam.pedro.presentation.component.NewActivityTopAppBar
import com.lam.pedro.presentation.component.StatsDisplay
import com.lam.pedro.presentation.component.TimerDisplay
import com.lam.pedro.presentation.component.TrainIntensitySelector
import com.lam.pedro.presentation.component.WaterGlass
import com.lam.pedro.presentation.component.YogaStyleSelector
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModelFactory
import com.lam.pedro.util.services.ActivityTrackingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun NewActivityScreen(
    onNavBack: () -> Unit,
    titleId: Int,
    viewModel: ActivitySessionViewModel,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory()),
    newActivityViewModel: NewActivityViewModel = viewModel(
        factory = NewActivityViewModelFactory(
            LocalContext.current,
            viewModel.activityEnum
        )
    )
) {

    Log.d(
        "TIPO DEL NEW ACTIVITY VIEW MODEL",
        "---- TIPO DEL NEW ACTIVITY VIEW MODEL: $newActivityViewModel"
    )

    Log.d(
        "TIPO DEL NEW ACTIVITY SCREEN",
        "---- TIPO DEL NEW ACTIVITY SCREEN: ${viewModel.activityEnum.activityType}"
    )
    // variables
    val color = viewModel.activityEnum.color
    val activityType = viewModel.activityEnum.activityType
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val sessionJob = remember { Job() }
    val sessionScope = remember { CoroutineScope(sessionJob + Dispatchers.Default) }


    newActivityViewModel.ExecuteFunctionalities()

    Scaffold(
        topBar = {
            NewActivityTopAppBar(
                titleId = titleId,
                onNavBack = { onNavBack() }
            )
        },
        snackbarHost = { CustomSnackbarHost(newActivityViewModel.snackbarHostState) }
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

            val timerResults = remember { mutableStateListOf<String>() }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (newActivityViewModel.activityTitle.value == "") "Press to start" else newActivityViewModel.activityTitle.value,
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
                                steps = newActivityViewModel.steps.floatValue,
                                averageSpeed = newActivityViewModel.averageSpeed.doubleValue,
                                distance = newActivityViewModel.distance.doubleValue,
                                color = color
                            )
                        }

                        ExerciseSessionRecord.EXERCISE_TYPE_BIKING, ExerciseSessionRecord.EXERCISE_TYPE_SURFING -> {
                            StatsDisplay(
                                averageSpeed = newActivityViewModel.averageSpeed.doubleValue,
                                distance = newActivityViewModel.distance.doubleValue,
                                color = color
                            )
                        }

                        ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> {
                            YogaStyleSelector(
                                yogaStyle = newActivityViewModel.yogaStyle.value,
                                onYogaStyleChange = { newStyle ->
                                    newActivityViewModel.yogaStyle.value = newStyle
                                },
                                color = color
                            )
                        }

                        ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS -> {
                            TrainIntensitySelector(
                                trainIntensity = newActivityViewModel.trainIntensity.value,
                                onTrainIntensityChange = { newStyle ->
                                    newActivityViewModel.trainIntensity.value = newStyle
                                },
                                color = color
                            )
                        }

                        ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR -> {
                            WaterGlass(hydrationVolume = newActivityViewModel.hydrationVolume.doubleValue) { addedVolume ->
                                newActivityViewModel.hydrationVolume.value += addedVolume
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
                    activityTitle = newActivityViewModel.activityTitle.value,
                    onTitleChange = {
                        //newActivityViewModel.activityTitle = it
                        newActivityViewModel.updateTitle(it)
                        //newActivityViewModel.isTitleEmpty.value = newActivityViewModel.activityTitle.value.isBlank()
                    },
                    isTitleEmpty = newActivityViewModel.isTitleEmpty,
                    notes = newActivityViewModel.notes.value,
                    onNotesChange = { newActivityViewModel.notes.value = it },
                    onConfirm = {
                        coroutineScope.launch {
                            if (newActivityViewModel.activityTitle.value.isNotBlank()) {
                                if (isStopAction) {
                                    context.stopService(
                                        Intent(
                                            context,
                                            ActivityTrackingService::class.java
                                        )
                                    )

                                    val endTime = ZonedDateTime.now()
                                    val duration = Duration.between(startTime, endTime).toMinutes()
                                    if (duration < 1) {
                                        showConfirmDialog = false
                                        coroutineScope.launch {
                                            val snackbarJob = launch {
                                                newActivityViewModel.snackbarHostState.showSnackbar(
                                                    message = "Activity must be at least 1 minute",
                                                    actionLabel = "OK",
                                                    duration = SnackbarDuration.Indefinite
                                                )
                                            }

                                            // Aspetta 1.5 secondi (durata personalizzata)
                                            delay(1500L)

                                            // Cancella manualmente lo Snackbar dopo il ritardo
                                            newActivityViewModel.snackbarHostState.currentSnackbarData?.dismiss()

                                            // Interrompi il Job dello Snackbar
                                            snackbarJob.cancel()

                                            // Naviga indietro e annulla il lavoro di sessione
                                            sessionJob.cancelAndJoin()
                                            onNavBack()
                                        }
                                    } else {
                                        newActivityViewModel.saveActivity(
                                            elapsedTime = elapsedTime,
                                            timerResults = timerResults,
                                            duration = duration,
                                            startTime = startTime,
                                            endTime = endTime,
                                            profileViewModel = profileViewModel,
                                            activitySessionViewModel = viewModel
                                        )
                                        sessionJob.cancelAndJoin()
                                        onNavBack()
                                    }

                                } else {
                                    visible = !visible
                                    if (visible) {
                                        isPaused = false
                                        timerRunning = true
                                    }
                                }
                                showConfirmDialog = false
                            } else {
                                newActivityViewModel.isTitleEmpty.value = true
                            }
                        }
                    }
                )
            }

            if (timerRunning && !isPaused) {
                LaunchedEffect(Unit) {
                    startTime = ZonedDateTime.now()

                    sessionScope.launch {
                        while (timerRunning && !isPaused) {
                            delay(10)
                            elapsedTime += 10
                        }
                    }

                    // Avvia il servizio di tracking solo se necessario
                    sessionScope.launch {
                        val serviceIntent = newActivityViewModel.startTrackingServiceIfNeeded(context)
                        if (serviceIntent != null) {
                            context.startService(serviceIntent)
                        }
                    }
                }
            }


        }
    }

}