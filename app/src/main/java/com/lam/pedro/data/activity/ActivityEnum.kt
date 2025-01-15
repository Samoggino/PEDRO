package com.lam.pedro.data.activity

import androidx.compose.ui.graphics.Color
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.R
import com.lam.pedro.presentation.theme.CyclingColor
import com.lam.pedro.presentation.theme.DriveColor
import com.lam.pedro.presentation.theme.LiftColor
import com.lam.pedro.presentation.theme.ListenColor
import com.lam.pedro.presentation.theme.RunColor
import com.lam.pedro.presentation.theme.SitColor
import com.lam.pedro.presentation.theme.SleepColor
import com.lam.pedro.presentation.theme.TrainColor
import com.lam.pedro.presentation.theme.UnknownColor
import com.lam.pedro.presentation.theme.WalkColor
import com.lam.pedro.presentation.theme.YogaColor
import kotlinx.serialization.Serializable

@Serializable
enum class ActivityEnum(
    val color: Color,
    val image: Int = 0,
    val energyMetrics: Boolean = false,
    val distanceMetrics: Boolean = false,
    val fullEnergyDistanceMetrics: Boolean = false,
    val activityType: Int = 1
) {
    CYCLING(
        color = CyclingColor,
        image = R.drawable.bicycling_icon,
        fullEnergyDistanceMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_BIKING
    ),
    RUN(
        color = RunColor,
        image = R.drawable.running_icon,
        fullEnergyDistanceMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING
    ),
    WALK(
        color = WalkColor,
        image = R.drawable.walking_icon,
        fullEnergyDistanceMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
    ),

    YOGA(
        color = YogaColor,
        image = R.drawable.yoga_icon,
        energyMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_YOGA
    ),
    TRAIN(
        color = TrainColor,
        image = R.drawable.stretching_icon,
        energyMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS
    ),
    DRIVE(
        color = DriveColor,
        image = R.drawable.car_icon,
        distanceMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_SURFING
    ),
    LIFT(
        color = LiftColor,
        image = R.drawable.dumbells_icon,
        energyMetrics = true,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING
    ),

    SIT(
        color = SitColor,
        image = R.drawable.armchair_icon,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR
    ),
    SLEEP(
        color = SleepColor,
        image = R.drawable.sleeping_icon,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_WATER_POLO
    ),
    LISTEN(
        color = ListenColor,
        image = R.drawable.headphones_icon,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING
    ),
    UNKNOWN(
        color = UnknownColor,
        image = R.drawable.unknown_icon,
        activityType = ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
    )
}