package com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels

import android.content.Context
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository
import com.lam.pedro.presentation.screen.activities.newActivity.NewActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.GpsFunctionality
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenFunctionality
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.StepCounterFunctionality

class NewSittingActivityViewModel(
    context: Context,
    repository: ActivityTrackingRepository
) : NewActivityViewModel(context, repository) {

    // Funzionalità specifiche per il sitting
    override val functionalities = emptyList<ScreenFunctionality>()

}
