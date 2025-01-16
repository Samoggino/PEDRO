package com.lam.pedro.presentation.onboarding

import androidx.annotation.DrawableRes
import com.lam.pedro.R

sealed class OnboardingModel(
    @DrawableRes val image: Int?,
    val title: String,
    val description: String,
) {

    data object FirstPage : OnboardingModel(
        image = R.drawable.pedro_initial,
        title = "Hola! I'm Pedro, your activity partner",
        description = "Record as many activity as you want, anywhere you want"
    )

    data object SecondPage : OnboardingModel(
        image = R.drawable.pedro_running,
        title = "Running, driving, sitting and more",
        description = "I need some permissions to work...\nbut I don't want to bother you now, I will ask for them only when I need"
    )

    data object ThirdPage : OnboardingModel(
        image = null,
        title = "Tell me more about you gringo...",
        description = "I will use info such age, height, etc due to a more accurate calories analysis"
    )

    data object FourthPage : OnboardingModel(
        image = R.drawable.pedro_final,
        title = "...vamos!",
        description = "Everything is setted up!"
    )


}