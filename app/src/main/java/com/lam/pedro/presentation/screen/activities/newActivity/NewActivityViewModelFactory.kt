package com.lam.pedro.presentation.screen.activities.newActivity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository

class NewActivityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewActivityViewModel(context, ActivityTrackingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
