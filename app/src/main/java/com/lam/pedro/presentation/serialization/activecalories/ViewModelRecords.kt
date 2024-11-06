@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class
)

package com.lam.pedro.presentation.serialization.activecalories

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.lam.pedro.data.StageSerializable
import com.lam.pedro.data.YogaRecord
import com.lam.pedro.data.datasource.SecurePreferencesManager.getAccessToken
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.data.serializers.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

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
                    put("data", Json.encodeToJsonElement(record))
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
        Log.d(
            "Supabase",
            "Stage serializzato e deserializzato sono uguali: ${stage == deserialized}"
        )


    }

    fun actionThree() {
        // crea un oggetto YogaRecord e prova a serializzarlo e deserializzarlo
        // Creazione di un oggetto ActiveCaloriesBurnedRecord con valori di esempio
        val activeCaloriesBurnedRecord = ActiveCaloriesBurnedRecord(
            startTime = Instant.now(),
            startZoneOffset = ZoneOffset.UTC,
            endTime = Instant.now().plusSeconds(3600), // 1 ora di durata
            endZoneOffset = ZoneOffset.UTC,
            energy = Energy.kilocalories(100.0) // Ad esempio, 100 kcal
        )

        // Creazione di un oggetto ExerciseCompletionGoal.DurationGoal con valore di esempio
        val durationGoal = ExerciseCompletionGoal.DurationGoal(
            duration = Duration.between(
                Instant.now(),
                Instant.now().plusSeconds(3600)
            )
        ) // Durata di 1 ora

        // Creazione di un oggetto TotalCaloriesBurnedRecord con valori di esempio
        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = Instant.now(),
            startZoneOffset = ZoneOffset.UTC,
            endTime = Instant.now().plusSeconds(3600),
            endZoneOffset = ZoneOffset.UTC,
            energy = Energy.kilocalories(100.0)
        )

        // Creazione di un oggetto ExerciseLap con valori di esempio
        val exerciseLap = ExerciseLap(
            startTime = Instant.now(),
            endTime = Instant.now().plusSeconds(3600),
            length = Length.meters(500.0) // Ad esempio, una distanza di 500 metri
        )

        // Creazione di un oggetto YogaRecord con i valori di esempio

        try {
            val yogaRecord = YogaRecord(
                calories = activeCaloriesBurnedRecord,
                durationGoal = durationGoal,
                totalCaloriesBurned = totalCaloriesBurnedRecord,
//                exerciseLap = exerciseLap
            )

            // prova a serializzare e deserializzare l'oggetto
            val serialized = Json.encodeToString(yogaRecord)

            Log.d("Supabase", "YogaRecord serializzato: $serialized")
            // verifica se sono uguali

            val deserialized = Json.decodeFromString<YogaRecord>(serialized)
            Log.d(
                "Supabase",
                "YogaRecord serializzato e deserializzato sono uguali: ${yogaRecord == deserialized}"
            )
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la creazione di YogaRecord: ${e.message}")
        }
//

//        Log.d("Supabase", "YogaRecord serializzato: $serialized")
//        Log.d("Supabase", "YogaRecord deserializzato: $deserialized")
//        Log.d("Supabase", "YogaRecord deserializzato: ${deserialized.calories}")
//        Log.d("Supabase", "YogaRecord deserializzato: ${deserialized.durationGoal}")
//        Log.d("Supabase", "YogaRecord deserializzato: ${deserialized.totalCaloriesBurned}")
//        Log.d("Supabase", "YogaRecord deserializzato: ${deserialized.exerciseLap}")
//
//
        Log.d("Supabase", "Funzione actionThree non implementata")

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
