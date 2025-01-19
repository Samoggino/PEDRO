package com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels

import android.content.Context
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository
import com.lam.pedro.presentation.screen.activities.newActivity.NewActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ActivityRecognitionFunctionality
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.GpsFunctionality

class NewCyclingActivityViewModel(
    context: Context,
    repository: ActivityTrackingRepository
) : NewActivityViewModel(context, repository) {

    // Funzionalità specifiche per il cycling
    override val functionalities = listOf(
        GpsFunctionality(context),
        ActivityRecognitionFunctionality(context)
    )

}
