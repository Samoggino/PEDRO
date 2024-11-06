package com.lam.pedro.presentation.serialization.activecalories

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.lam.pedro.data.StageSerializable
import com.lam.pedro.data.datasource.SecurePreferencesManager.getAccessToken
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.data.serialize
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Instant

class ViewModelRecords : ViewModel() {

    suspend fun actionOne(
        navController: NavController,
        record: ActiveCaloriesBurnedRecord,
        context: Context
    ): List<ActiveCaloriesBurnedRecord> {

        if (getAccessToken(context) == null) {
            navController.navigate(Screen.LoginScreen.route)
        }

        val uuid = getUUID(context)

        try {

            // Crea un oggetto JSON completo con i dati della sessione di sonno e l'UUID dell'utente
            val jsonFinal = buildJsonObject {
                put("input_data", buildJsonObject {
                    put("data", Json.encodeToJsonElement(record.serialize()))
                    put("user_UUID", Json.encodeToJsonElement(uuid))
                })
            }

            Log.d("Supabase", "Dati di esercizio da caricare $jsonFinal")

            // Esegui la chiamata RPC su Supabase
            supabase()
                .postgrest
                .rpc("active_calories_burned", jsonFinal)

            val response = supabase()
                .from("active_calories_burned")
                .select()
                .decodeList<ActiveCaloriesBurnedRecord>()

            Log.d("Supabase", "Dati caricati con successo")

            return response
        } catch (e: Exception) {
            Log.e(
                "Supabase",
                "Errore durante il caricamento dei dati: ${e.message}",
            )
        }

        return emptyList()
    }

    fun actionTwo() {
        // crea un oggetto stage e prova a serializzarlo e deserializzarlo
        val stage = StageSerializable(Instant.now(), Instant.now(), 2)
        val serialized = Json.encodeToString(stage)
        val deserialized = Json.decodeFromString<StageSerializable>(serialized)
        Log.d("Supabase", "Stage serializzato: $serialized")
        Log.d("Supabase", "Stage deserializzato: $deserialized")
        Log.d("Supabase", "Stage deserializzato: ${deserialized.startTime}")
        Log.d("Supabase", "Stage deserializzato: ${deserialized.endTime}")
        Log.d("Supabase", "Stage deserializzato: ${deserialized.stage}")

        // verifica se sono uguali
        Log.d("Supabase", "Stage serializzato e deserializzato sono uguali: ${stage == deserialized}")



    }

    fun actionThree() {
        // Azione per Bottone 3
        println("Bottone 3 premuto")
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
