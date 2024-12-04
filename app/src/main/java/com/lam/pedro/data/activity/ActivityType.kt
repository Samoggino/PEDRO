package com.lam.pedro.data.activity

import androidx.compose.ui.graphics.Color
import com.lam.pedro.presentation.theme.CyclingColor
import com.lam.pedro.presentation.theme.DriveColor
import com.lam.pedro.presentation.theme.LiftColor
import com.lam.pedro.presentation.theme.ListenColor
import com.lam.pedro.presentation.theme.RunColor
import com.lam.pedro.presentation.theme.SitColor
import com.lam.pedro.presentation.theme.SleepColor
import com.lam.pedro.presentation.theme.TrainColor
import com.lam.pedro.presentation.theme.WalkColor
import com.lam.pedro.presentation.theme.YogaColor
import kotlinx.serialization.Serializable

@Serializable
enum class ActivityType(val color: Color, val energyMetrics: Boolean = false, val distanceMetrics: Boolean = false) {
    CYCLING(CyclingColor, energyMetrics = true, distanceMetrics = true),
    RUN(RunColor, energyMetrics = true, distanceMetrics = true),
    YOGA(YogaColor, energyMetrics = true),
    TRAIN(TrainColor, energyMetrics = true),
    DRIVE(DriveColor, distanceMetrics = true),
    SIT(SitColor),
    SLEEP(SleepColor),
    WALK(WalkColor, distanceMetrics = true, energyMetrics = true),
    LIFT(LiftColor, energyMetrics = true),
    LISTEN(ListenColor)
}