package com.lam.pedro.presentation.serialization

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.activitySupabase.IActivitySupabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyRecordsViewModel(private val activityRepository: IActivitySupabaseRepository) :
    ViewModel() {

    val tag = "Supabase"

    /**
     * Inserisce una sessione di attività nel database.
     * Funzione di debug per simulare l'inserimento di attività senza l'interfaccia utente e
     * senza richiedere permessi.
     * @param activityEnum Enum dell'attività da inserire
     */
    fun insertActivitySession(activityEnum: ActivityEnum) {
        // crea un numero casuale di attività
        val activities = (0..(1..5).random()).map {
            SessionCreator.createActivity(activityEnum)
        }
        insertActivitySession(activities)

    }

    /**
     * Inserisce una lista di sessioni di attività nel database.
     * @param genericActivities Lista di attività da inserire
     */
    fun insertActivitySession(genericActivities: List<GenericActivity>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                activityRepository.insertActivitySession(genericActivities, getUUID()!!)
            } catch (e: Exception) {
                Log.e(tag, "Errore durante l'inserimento", e)
            }
        }
    }
}

class MyScreenRecordsFactory(private val activityRepository: IActivitySupabaseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyRecordsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyRecordsViewModel(activityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


