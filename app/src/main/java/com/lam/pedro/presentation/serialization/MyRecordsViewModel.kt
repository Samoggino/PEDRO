package com.lam.pedro.presentation.serialization

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.activitySupabase.IActivitySupabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyRecordsViewModel(private val activityRepository: IActivitySupabaseRepository) : ViewModel() {

    val tag = "Supabase"

    // LiveData per monitorare lo stato dell'import
    private val _importResult = MutableLiveData<ResultState>(ResultState.Idle)
    val importResult: LiveData<ResultState> = _importResult

    // LiveData per monitorare lo stato del salvataggio
    private val _saveResult = MutableLiveData<ResultState>(ResultState.Idle)
    val saveResult: LiveData<ResultState> = _saveResult

    // LiveData per il messaggio da visualizzare (es. Toast)
    val dataMutable = MutableLiveData<String?>()
    val messageEvent: LiveData<String?> = dataMutable

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
    private fun insertActivitySession(genericActivities: List<GenericActivity>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                activityRepository.insertActivitySession(genericActivities, getUUID()!!)
                _saveResult.postValue(ResultState.Success)
            } catch (e: Exception) {
                Log.e(tag, "Errore durante l'inserimento", e)
                _saveResult.postValue(ResultState.Error)
            }
        }
    }

    /**
     * Funzione per esportare i dati dal DB
     */
    fun exportFromDB() {
        _saveResult.value = ResultState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = activityRepository.exportDataFromDB(getUUID()!!)
                val isSuccess = JsonDBManager.saveExportedJSON(json)

                _saveResult.postValue(
                    if (isSuccess) ResultState.Success else ResultState.Error
                )

                // Aggiorna il messaggio da mostrare (Toast)
                dataMutable.postValue(
                    if (isSuccess) "File salvato nella cartella Download" else "Errore durante il salvataggio"
                )
            } catch (e: Exception) {
                _saveResult.postValue(ResultState.Error)
                dataMutable.postValue("Errore durante l'esportazione")
            }
        }
    }

    /**
     * Funzione per importare i dati dal file JSON nel DB
     */
    fun importJsonToDatabase(uri: Uri, userUUID: String = getUUID()!!) {
        _importResult.value = ResultState.Loading // Stato iniziale: caricamento

        viewModelScope.launch(Dispatchers.IO) {
            try {
                activityRepository.importJsonToDatabase(uri = uri, userUUID = userUUID)
                _importResult.postValue(ResultState.Success)
                dataMutable.postValue("Import completato con successo")
            } catch (e: Exception) {
                _importResult.postValue(ResultState.Error)
                dataMutable.postValue("Errore durante l'import: ${e.message}")
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


