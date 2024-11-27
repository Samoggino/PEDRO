package com.lam.pedro.presentation.serialization

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityInterface
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.safeRpcCall
import com.lam.pedro.presentation.serialization.SessionCreator.activityConfigs
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

class ViewModelRecords : ViewModel() {

    /**
     * Recupera una sessione di attività dal database in base al tipo passato in input
     * @param context il contesto dell'applicazione
     * @param activityType il tipo di attività da recuperare
     */
    @OptIn(InternalSerializationApi::class)
    fun getActivitySession(
        context: Context,
        activityType: ActivityType
    ) {
        Log.d("Supabase", "Recupero delle attività di tipo $activityType")
        val config = activityConfigs[activityType] as? SessionCreator.ActivityConfig<*>

        if (config != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val uuid = getUUID(context).toString()
                try {
                    val responseJson = (
                            safeRpcCall(
                                rpcFunctionName = "get_${activityType.name.lowercase()}_session",
                                jsonFinal = buildJsonObject {
                                    put("user_uuid", Json.encodeToJsonElement(uuid))
                                }) as? PostgrestResult)
                        ?.data

                    // Usa il tipo specifico per deserializzare
                    val response: List<ActivityInterface> = Json.decodeFromString(
                        ListSerializer(config.responseType.serializer()),
                        responseJson!!
                    )

                    Log.d("Supabase", "Dati delle attività: $response")
                } catch (e: Exception) {
                    Log.e("Supabase-HealthConnect", "Errore durante il recupero delle attività: $e")
                }
            }
        } else {
            Log.e("Supabase", "Configurazione non trovata per $activityType")
        }
    }


    /**
     * Inserisce una sessione di attività nel database in base al tipo passato in input
     * @param context il contesto dell'applicazione
     * @param activityType il tipo di attività da inserire
     */
    fun insertActivitySession(context: Context, activityType: ActivityType) {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = getUUID(context).toString()
            val config = activityConfigs[activityType]

            if (config != null) {
                try {
                    // Crea la nuova sessione usando il creator specifico
                    val newSession = config.sessionCreator()

                    val jsonFinal = buildJsonObject {
                        put("input_data", buildJsonObject {
                            put("data", Json.encodeToJsonElement(newSession))
                            put("user_UUID", uuid) // Passa la stringa direttamente
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


