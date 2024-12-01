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
enum class ActivityType(val color: Color) {
    CYCLING(CyclingColor),
    RUN(RunColor),
    YOGA(YogaColor),
    TRAIN(TrainColor),
    DRIVE(DriveColor),
    SIT(SitColor),
    SLEEP(SleepColor),
    WALK(WalkColor),
    LIFT(LiftColor),
    LISTEN(ListenColor)
}