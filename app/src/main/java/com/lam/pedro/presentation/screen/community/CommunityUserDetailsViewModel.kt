package com.lam.pedro.presentation.screen.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.serialization.MyRecordsViewModel
import com.lam.pedro.presentation.serialization.MyScreenRecordsFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class CommunityUserDetailsViewModel : ViewModel() {
    val activityMap = MutableStateFlow<Map<ActivityEnum, List<GenericActivity>>>(emptyMap())
    val viewModel = MyScreenRecordsFactory().create(MyRecordsViewModel::class.java)

    fun fetchActivityMap(userUUID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            activityMap.value = viewModel.getActivityMap(userUUID = userUUID)
        }
    }
}

class CommunityUserDetailsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityUserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityUserDetailsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}