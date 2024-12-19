package com.lam.pedro.util

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import java.time.ZonedDateTime

suspend fun stopActivity(
    timerRunning: MutableState<Boolean>,
    visible: MutableState<Boolean>,
    isPaused: MutableState<Boolean>,
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
    distance: MutableState<Double>,
    exerciseRoute: List<ExerciseRoute.Location>,
    viewModel: ActivitySessionViewModel
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

    viewModel.saveSession(viewModel.actualSession)

    viewModel.fetchSessions(viewModel.activityType)

    /*
    when (viewModel.activityType) {
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> {
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
                exerciseRoute = ExerciseRoute(exerciseRoute)
            )
            Log.d("TAG", "------------Run session: $runSession")
            viewModel.saveSession(runSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> {
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
            val walkSession = WalkSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes,
                speedSamples = speedSamples,
                stepsCount = steps.toLong(),
                totalEnergy = Energy.calories(totalCalories),
                activeEnergy = Energy.calories(activeCalories),
                distance = Length.meters(distance.value),
                exerciseRoute = ExerciseRoute(exerciseRoute)
            )
            Log.d("TAG", "------------Walk session: $walkSession")
            viewModel.saveSession(walkSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> {
            val (totalCalories, activeCalories) = calculateCyclingCalories(
                profileViewModel.weight.toDouble(),
                profileViewModel.height.toDouble(),
                profileViewModel.age.toInt(),
                profileViewModel.sex,
                distance.value,
                duration,
                averageSpeed
            )
            val cycleSession = CycleSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes,
                speedSamples = speedSamples, // Non disponibile in ExerciseSessionRecord
                totalEnergy = Energy.calories(totalCalories), // Fallback
                activeEnergy = Energy.calories(activeCalories), // Fallback
                distance = Length.meters(distance.value), // Fallback
                exerciseRoute = ExerciseRoute(exerciseRoute) //Fallback
            )
            Log.d("TAG", "------------Cycle session: $cycleSession")
            viewModel.saveSession(cycleSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_SURFING -> {
            val driveSession = DriveSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes,
                speedSamples = speedSamples,
                distance = Length.meters(distance.value),
                exerciseRoute = ExerciseRoute(exerciseRoute)
            )
            Log.d("TAG", "------------Drive session: $driveSession")
            viewModel.saveSession(driveSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING -> {
            val listenSession = ListenSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes
            )
            Log.d("TAG", "------------Listen session: $listenSession")
            viewModel.saveSession(listenSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR -> {
            val sitSession = SitSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes,
                volume = Volume.liters(hydrationVolume)
            )
            Log.d("TAG", "------------Sit session: $sitSession")
            viewModel.saveSession(sitSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT -> {
            val sleepSession = SleepSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes
            )
            Log.d("TAG", "------------Sleep session: $sleepSession")
            viewModel.saveSession(sleepSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS -> {
            val (totalCalories, activeCalories) = calculateTrainCalories(
                profileViewModel.weight.toDouble(),
                profileViewModel.height.toDouble(),
                profileViewModel.age.toInt(),
                profileViewModel.sex,
                duration,
                trainIntensity
            )
            val trainSession = TrainSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes,
                totalEnergy = Energy.calories(totalCalories),
                activeEnergy = Energy.calories(activeCalories),
                exerciseSegment = listOf(),//TODO
                exerciseLap = listOf()//TODO
            )
            Log.d("TAG", "------------Train session: $trainSession")
            viewModel.saveSession(trainSession)
        }
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> {
            val (totalCalories, activeCalories) = calculateYogaCalories(
                profileViewModel.weight.toDouble(),
                profileViewModel.height.toDouble(),
                profileViewModel.age.toInt(),
                profileViewModel.sex,
                duration,
                yogaStyle
            )
            val yogaSession = YogaSession(
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                title = activityTitle,
                notes = notes,
                totalEnergy = Energy.calories(totalCalories),
                activeEnergy = Energy.calories(activeCalories),
                exerciseSegment = listOf(),//TODO
                exerciseLap = listOf()//TODO
            )
            Log.d("TAG", "------------Yoga session: $yogaSession")
            viewModel.saveSession(yogaSession)
        }
    }

     */



}
