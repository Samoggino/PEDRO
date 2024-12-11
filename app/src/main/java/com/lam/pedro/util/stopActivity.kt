package com.lam.pedro.util

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.navigation.NavController
import com.lam.pedro.data.activitySession.RunSession
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import java.time.Duration
import java.time.ZonedDateTime

suspend fun stopActivity(
    timerRunning: MutableState<Boolean>,
    visible: MutableState<Boolean>,
    isPaused: MutableState<Boolean>,
    elapsedTime: Int,
    timerResults: MutableList<String>,
    startTime: ZonedDateTime,
    activityTitle: String,
    notes: String,
    speedSamples: List<SpeedRecord.Sample>,
    steps: Float,
    profileViewModel: ProfileViewModel,
    distance: MutableState<Double>,
    exerciseRoute: List<ExerciseRoute.Location>,
    titleId: Int,
    viewModel: ActivitySessionViewModel,
    sessionJob: Job,
    navController: NavController,
    activityType: Int
) {
    timerRunning.value = false
    visible.value = false
    isPaused.value = true

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
    Log.d("TAG", "------------Timer results: $timerResults")

    var endTime = ZonedDateTime.now()

    val duration = Duration.between(startTime, endTime).toMinutes()
    val averageSpeed = calculateAverageSpeed(speedSamples)

    if (titleId == Screen.RunSessionScreen.titleId) {
        val (totalCalories, activeCalories) = calculateCalories(
            profileViewModel.weight.toDouble(),
            profileViewModel.height.toDouble(),
            profileViewModel.age.toInt(),
            profileViewModel.sex,
            distance.value,
            steps.toInt(),
            duration,
            averageSpeed
        )
        val runSession = RunSession(
            startTime = startTime.toInstant(),
            endTime = endTime.toInstant(),
            title = activityTitle,
            notes = notes,
            speedSamples = speedSamples,
            stepsCount = steps.toLong(),
            totalEnergy = Energy.calories(totalCalories),
            activeEnergy = Energy.calories(activeCalories),
            distance = Length.meters(distance.value),
            //elevationGained = Length.meters(3.0),
            exerciseRoute = ExerciseRoute(exerciseRoute)
        )
        Log.d("TAG", "------------Run session: $runSession")
        viewModel.saveRunSession(runSession)

        sessionJob.cancelAndJoin()
    }

    viewModel.fetchExerciseSessions(activityType)

    navController.popBackStack()
}
