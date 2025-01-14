package com.lam.pedro.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavigationTransitions {

    // Transizione di fade-in con durata personalizzabile
    fun fadeIn(durationMillis: Int = 700): EnterTransition =
        fadeIn(animationSpec = tween(durationMillis))

    // Transizione di fade-out con durata personalizzabile
    fun fadeOut(durationMillis: Int = 600): ExitTransition =
        fadeOut(animationSpec = tween(durationMillis))

    // Transizione di slide-in orizzontale da destra
    fun slideInHorizontally(durationMillis: Int = 700): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis)
        )

    // Transizione di slide-out orizzontale verso destra
    fun slideOutHorizontally(durationMillis: Int = 600): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis)
        )
}
