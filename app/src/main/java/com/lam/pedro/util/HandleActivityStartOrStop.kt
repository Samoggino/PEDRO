package com.lam.pedro.util

import android.util.Log
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModel

/*
fun handleActivityStartOrStop(
    title: String,
    notes: String,
    isStopAction: Boolean,
    onStop: () -> Unit,
    profileViewModel: ProfileViewModel,
    viewModel: ActivitySessionViewModel
) {
    if (isStopAction) {
        // Logica di Stop
        viewModel.stopActivitySession(
            onSuccess = {
                profileViewModel.refreshUserData()
                onStop()
            },
            onError = { error -> Log.e("ActivitySession", "Stop failed: $error") }
        )
    } else {
        // Logica di Start
        if (title.isBlank()) {
            Log.w("ActivitySession", "Title cannot be empty")
            return
        }
        viewModel.startActivitySession(
            title = title,
            notes = notes,
            activityType = 0, // Puoi eventualmente usare il parametro activityType passato
            onSuccess = { Log.d("ActivitySession", "Session started") },
            onError = { error -> Log.e("ActivitySession", "Start failed: $error") }
        )
    }
}

 */

