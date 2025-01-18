package com.lam.pedro.presentation.screen.activities.newActivity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.SessionState
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewCyclingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewRunningActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewYogaActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewWalkingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewTrainingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewDrivingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewLiftingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewSittingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewSleepingActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewListeningActivityViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.newActivityViewModels.NewUnknownActivityViewModel



class NewActivityViewModelFactory(
    private val context: Context,
    private val activityEnum: ActivityEnum
) : ViewModelProvider.Factory {

    private val repository = ActivityTrackingRepository

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (activityEnum) {
            ActivityEnum.CYCLING -> NewCyclingActivityViewModel(context, repository)
            ActivityEnum.RUN -> NewRunningActivityViewModel(context, repository)
            ActivityEnum.YOGA -> NewYogaActivityViewModel(context, repository)
            ActivityEnum.WALK -> NewWalkingActivityViewModel(context, repository)
            ActivityEnum.TRAIN -> NewTrainingActivityViewModel(context, repository)
            ActivityEnum.DRIVE -> NewDrivingActivityViewModel(context, repository)
            ActivityEnum.LIFT -> NewLiftingActivityViewModel(context, repository)
            ActivityEnum.SIT -> NewSittingActivityViewModel(context, repository)
            ActivityEnum.SLEEP -> NewSleepingActivityViewModel(context, repository)
            ActivityEnum.LISTEN -> NewListeningActivityViewModel(context, repository)
            ActivityEnum.UNKNOWN -> NewUnknownActivityViewModel(context, repository)
        } as T
    }
}


