package com.lam.pedro.presentation.screen.more.importExport

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.activitySupabase.IActivitySupabaseRepository
import com.lam.pedro.presentation.serialization.JsonDBManager
import com.lam.pedro.presentation.serialization.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImportExportViewModel(private val activityRepository: IActivitySupabaseRepository) :
    ViewModel() {

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


@Suppress("UNCHECKED_CAST")
class ImportExportViewModelFactory(
    private val activityRepository: IActivitySupabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImportExportViewModel(activityRepository) as T
    }
}
