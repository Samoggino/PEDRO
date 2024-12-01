package com.lam.pedro.presentation.charts

//
//class ViewModelCharts(
//    private val repository: ActivityRepository
//) : ViewModel() {
//
//    private val _activities = MutableStateFlow<List<GenericActivity>>(emptyList())
//    val activities: StateFlow<List<GenericActivity>> = _activities
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    fun loadActivitySession(activityType: ActivityType) {
//        viewModelScope.launch {
//            val result = repository.getActivitySession(activityType)
//            result.fold(
//                onSuccess = { _activities.value = it },
//                onFailure = { _error.value = it.localizedMessage }
//            )
//        }
//    }
//}
//
//
//
//
//@Suppress("UNCHECKED_CAST")
//class ViewModelRecordFactory :
//    ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ViewModelCharts::class.java)) {
//            return ViewModelCharts(
//                ActivityRepository()
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
