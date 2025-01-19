package com.lam.pedro.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import java.time.ZonedDateTime

@SuppressLint("DefaultLocale")
suspend fun stopActivity(
    elapsedTime: Int,
    timerResults: MutableList<String>,
    duration: Long,
    startTime: ZonedDateTime,
    endTime: ZonedDateTime,
    activityTitle: String,
    notes: String,
    speedSamples: List<SpeedRecord.Sample>,
    steps: Float,
    hydrationVolume: Double,
    trainIntensity: String,
    yogaStyle: String,
    profileViewModel: ProfileViewModel,
    distance: Double,
    exerciseRoute: List<ExerciseRoute.Location>,
    viewModel: ActivitySessionViewModel
) {

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

    val averageSpeed = calculateAverageSpeed(speedSamples)

    Log.d("ACTIVITY_TYPE", "------------ActivityType: $viewModel.activityType")

    viewModel.createSession(
        duration,
        startTime,
        endTime,
        activityTitle,
        notes,
        speedSamples,
        steps,
        hydrationVolume,
        trainIntensity,
        yogaStyle,
        profileViewModel,
        distance,
        exerciseRoute,
    )

    Log.d("STOP_ACTIVITY", "------------Session created: ${viewModel.actualSession}")

    viewModel.saveSession(viewModel.actualSession)

    viewModel.fetchSessions()
}