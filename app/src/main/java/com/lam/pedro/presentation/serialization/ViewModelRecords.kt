package com.lam.pedro.presentation.serialization

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.safeRpcCall
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.serialization.SessionCreator.activityConfigs
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

object ViewModelRecords : ViewModel() {

    const val tag = "Supabase"

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
     * Recupera le sessioni di attività dal database in base al tipo di attività specificato.
     * @param activityEnum Enum dell'attività da recuperare
     * @return Lista di attività di tipo [GenericActivity]
     */
    @OptIn(InternalSerializationApi::class)
    suspend fun getActivitySession(
        activityEnum: ActivityEnum,
        uuid: String? = getUUID()!!
    ): List<GenericActivity> {

        Log.d(tag, "Recupero delle attività di tipo $activityEnum")
        val config = activityConfigs[activityEnum] as? SessionCreator.ActivityConfig<*>

        if (config != null) {
            return withContext(Dispatchers.IO) {
                try {
                    Json.decodeFromString(
                        ListSerializer(config.responseType.serializer()),
                        (safeRpcCall(
                            rpcFunctionName = "get_${activityEnum.name.lowercase()}_session",
                            buildJsonObject {
                                put("user_uuid", Json.encodeToJsonElement(uuid))
                            }
                        ) as? PostgrestResult)?.data!!
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Errore durante il recupero delle attività: $e")
                    emptyList()
                }
            }
        } else {
            Log.e(tag, "Configurazione non trovata per $activityEnum")
            return emptyList()
        }
    }


    private val _activityMap = MutableLiveData<Map<ActivityEnum, List<GenericActivity>>>()


    suspend fun getActivityMap(userUUID: String): Map<ActivityEnum, List<GenericActivity>> {
        val map = mutableMapOf<ActivityEnum, List<GenericActivity>>()
        ActivityEnum.entries.forEach { activityEnum ->
            map[activityEnum] = getActivitySession(activityEnum, userUUID)
        }
        _activityMap.postValue(map)
        return map
    }


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
        Log.d(
            tag,
            "Activities json: ${
                Json.encodeToString(
                    ListSerializer(GenericActivity.serializer()),
                    activities
                )
            }"
        )
        insertActivitySession(activities)

    }

    /**
     * Inserisce una lista di sessioni di attività nel database.
     * @param genericActivities Lista di attività da inserire
     */
    fun insertActivitySession(genericActivities: List<GenericActivity>) {
        viewModelScope.launch(Dispatchers.IO) {
            genericActivities.forEach { activity ->
                try {
                    // Inserisci la sessione nel database
                    safeRpcCall(
                        rpcFunctionName = "insert_${activity.activityEnum.name.lowercase()}_session",
                        createActivityJson(activity, getUUID()!!)
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Errore durante l'inserimento: ${activity.activityEnum.name}", e)
                }
            }
        }
    }

    private fun createActivityJson(activity: GenericActivity, userUUID: String): JsonObject {
        return buildJsonObject {
            put("input_data", buildJsonObject {
                put("data", Json.encodeToJsonElement(activity))
                put("user_UUID", userUUID)
            })
        }
    }


    // Funzione per esportare i dati dal DB
    fun exportFromDB() {
        _saveResult.value = ResultState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val json = supabase()
                .from("activity")
                .select(
                    columns = Columns.list(
                        "user_id",
                        "activity_type",
                        "start_time",
                        "end_time",
                        "notes",
                        "title",
                        "JSON"
                    )
                ) {
                    filter { eq("user_id", getUUID() ?: "") }
                }
                .decodeList<JsonObject>().toString()

            val isSuccess = JsonDBManager.saveExportedJSON(json)

            _saveResult.postValue(
                if (isSuccess) ResultState.Success else ResultState.Error
            )

            // Aggiorna il messaggio da mostrare (Toast)
            dataMutable.postValue(
                if (isSuccess) "File salvato nella cartella Download" else "Errore durante il salvataggio"
            )
        }
    }

    fun importJsonToDatabase(uri: Uri) {
        _importResult.value = ResultState.Loading // Stato iniziale: caricamento

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream =
                    SecurePreferencesManager.appContext!!.contentResolver.openInputStream(uri)
                        ?: throw Exception("Impossibile leggere il file, URI non valido.")

                val jsonObject =
                    Json.parseToJsonElement(inputStream.bufferedReader().use { it.readText() })
                        .jsonArray
                        .map { it.jsonObject }

                // Inserimento dati nel DB
                insertActivitySession(JsonDBManager.fromJsonListToGenericActivityList(jsonObject))

                withContext(Dispatchers.IO) {
                    inputStream.close()
                }

                // Stato di successo
                _importResult.postValue(ResultState.Success)
                dataMutable.postValue("Import completato con successo")

            } catch (e: Exception) {
                // Stato di errore
                _importResult.postValue(ResultState.Error)
                dataMutable.postValue("Errore durante l'import: ${e.message}")
            }
        }
    }

}


