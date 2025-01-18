package com.lam.pedro.presentation.screen.community.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.activitySupabase.IActivitySupabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserCommunityDetailsViewModel(
    private val userUUID: String,
    private val activityRepository: IActivitySupabaseRepository // Repository iniettato
) : ViewModel() {

    val activityMap = MutableStateFlow<Map<ActivityEnum, List<GenericActivity>>>(emptyMap())
    val isLoading = MutableStateFlow(false) // Stato di caricamento

    // se fetchActivityMap() ritorna una mappe vuota, allora devo impostare uno stato a false per non mostrare delle cose
    val isDataAvailable = MutableStateFlow(false)

    init {
        fetchActivityMap()
    }

    private fun fetchActivityMap() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.value = true // Avvia il caricamento
                activityMap.value = getActivityMap(userUUID = userUUID) // Chiama il repository

                if (activityMap.value.any { it.value.isNotEmpty() }) {
                    isDataAvailable.value = true // Se ci sono voci non vuote, attiva il bottone
                }


            } catch (e: Exception) {
                Log.e("Community", "Errore nel fetch dei dati: ${e.message}")
            } finally {
                isLoading.value = false // Concludi il caricamento
            }
        }
    }

    // Funzione per recuperare le sessioni di attività tramite il repository
    private suspend fun getActivityMap(userUUID: String): Map<ActivityEnum, List<GenericActivity>> {
        val map = mutableMapOf<ActivityEnum, List<GenericActivity>>()
        ActivityEnum.entries.forEach { activityEnum ->
            // Usa il repository
            map[activityEnum] = activityRepository.getActivitySession(activityEnum, userUUID)
        }
        return map
    }

    private val _showPieChart = MutableStateFlow(false)
    val showPieChart: StateFlow<Boolean> = _showPieChart

    fun togglePieChart() {
        _showPieChart.value = !_showPieChart.value
    }
}


class UserCommunityDetailsViewModelFactory(
    private val userUUID: String,
    private val activityRepository: IActivitySupabaseRepository // Repository da iniettare
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserCommunityDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserCommunityDetailsViewModel(userUUID, activityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
