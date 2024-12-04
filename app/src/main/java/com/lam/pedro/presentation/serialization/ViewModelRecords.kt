package com.lam.pedro.presentation.serialization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.safeRpcCall
import com.lam.pedro.presentation.serialization.SessionCreator.activityConfigs
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

open class ViewModelRecords : ViewModel() {

    // LiveData per osservare il risultato delle sessioni di attività
    private val _activitySessions = MutableLiveData<List<GenericActivity>>()
    open val activitySessions: LiveData<List<GenericActivity>> = _activitySessions

    // LiveData per gestire l'errore
    private val _error = MutableLiveData<String>()
    open val error: LiveData<String> = _error

    /**
     * Recupera una sessione di attività dal database in base al tipo passato in input
     * @param activityType il tipo di attività da recuperare
     */
    fun loadActivitySession(activityType: ActivityType) {
        viewModelScope.launch {
            try {
                val sessions = getActivitySession(activityType)
                _activitySessions.postValue(sessions)  // Aggiorna LiveData
            } catch (e: Exception) {
                _error.postValue("Errore durante il recupero delle attività: ${e.message}")
            }
        }
    }


    @OptIn(InternalSerializationApi::class)
    suspend fun getActivitySession(
        activityType: ActivityType
    ): List<GenericActivity> {
        Log.d("Supabase", "Recupero delle attività di tipo $activityType")
        val config = activityConfigs[activityType] as? SessionCreator.ActivityConfig<*>

        if (config != null) {
            return withContext(Dispatchers.IO) {
                val uuid = getUUID()
                try {
                    val responseJson = (
                            safeRpcCall(
                                rpcFunctionName = "get_${activityType.name.lowercase()}_session",
                                jsonFinal = buildJsonObject {
                                    put("user_uuid", Json.encodeToJsonElement(uuid))
                                }) as? PostgrestResult
                            )?.data

                    val response: List<GenericActivity> = Json.decodeFromString(
                        ListSerializer(config.responseType.serializer()),
                        responseJson!!
                    )

                    response
                } catch (e: Exception) {
                    Log.e("Supabase-HealthConnect", "Errore durante il recupero delle attività: $e")
                    emptyList()
                }
            }
        } else {
            Log.e("Supabase", "Configurazione non trovata per $activityType")
            return emptyList()
        }
    }

    /**
     * Inserisce una sessione di attività nel database in base al tipo passato in input
     * @param activityType il tipo di attività da inserire
     */
    fun insertActivitySession(activityType: ActivityType) {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = getUUID().toString()
            val config = activityConfigs[activityType]

            if (config != null) {
                try {
                    // Crea la nuova sessione usando il creator specifico
                    val newSession = config.sessionCreator()

                    val jsonFinal = buildJsonObject {
                        put("input_data", buildJsonObject {
                            put("data", Json.encodeToJsonElement(newSession))
                            put("user_UUID", uuid)
                        })
                    }

                    Log.d("Supabase", "Inserimento della sessione: $jsonFinal")

                    // Inserisci la sessione nel database come oggetto JSON
                    safeRpcCall("insert_${activityType.name.lowercase()}_session", jsonFinal)

                    Log.d("Supabase", "Sessione inserita con successo: $newSession")

                } catch (e: Exception) {
                    Log.e(
                        "Supabase-HealthConnect",
                        "Errore durante l'inserimento della sessione: $e"
                    )
                }
            } else {
                Log.e("Supabase", "Configurazione non trovata per $activityType")
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
class ViewModelRecordFactory :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelRecords::class.java)) {
            return ViewModelRecords() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


