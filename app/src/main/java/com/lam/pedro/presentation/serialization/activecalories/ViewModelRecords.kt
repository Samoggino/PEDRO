@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class
)

package com.lam.pedro.presentation.serialization.activecalories

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.lam.pedro.data.RunData
import com.lam.pedro.data.YogaData
import com.lam.pedro.data.datasource.SecurePreferencesManager.getAccessToken
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
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
        val stage = SleepSessionRecord.Stage(Instant.now(), Instant.now(), 2)

        // verifica se sono uguali
        checkSerialization(stage)

    }

    fun actionThree(context: Context) {
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
            val yogaData = YogaData(
                calories = activeCaloriesBurnedRecord,
                durationGoal = durationGoal,
                totalCaloriesBurned = totalCaloriesBurnedRecord,
                exerciseLap = exerciseLap
            )

            checkSerialization(yogaData)

        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la creazione di YogaRecord: ${e.message}")
        }
    }


    fun actionFour(context: Context) {
        // crea un oggetto RunData e prova a serializzarlo e deserializzarlo
        val runData = RunData(
            calories = ActiveCaloriesBurnedRecord(
                startTime = Instant.now(),
                startZoneOffset = ZoneOffset.UTC,
                endTime = Instant.now().plusSeconds(3600),
                endZoneOffset = ZoneOffset.UTC,
                energy = Energy.kilocalories(100.0)
            ),
            totalCaloriesBurned = TotalCaloriesBurnedRecord(
                startTime = Instant.now(),
                startZoneOffset = ZoneOffset.UTC,
                endTime = Instant.now().plusSeconds(3600),
                endZoneOffset = ZoneOffset.UTC,
                energy = Energy.kilocalories(100.0)
            ),
            distanceRecord = DistanceRecord(
                startTime = Instant.now(),
                startZoneOffset = ZoneOffset.UTC,
                endTime = Instant.now().plusSeconds(3600),
                endZoneOffset = ZoneOffset.UTC,
                distance = Length.meters(500.0)
            ),
            elevationGainedRecord = ElevationGainedRecord(
                startTime = Instant.now(),
                startZoneOffset = ZoneOffset.UTC,
                endTime = Instant.now().plusSeconds(3600),
                endZoneOffset = ZoneOffset.UTC,
                elevation = Length.meters(100.0)
            ),
            exerciseRoute = ExerciseRoute(
                route = listOf(
                    ExerciseRoute.Location(
                        time = Instant.now(),
                        latitude = 0.0,
                        longitude = 0.0,
                        horizontalAccuracy = Length.meters(10.0),
                        verticalAccuracy = Length.meters(10.0),
                        altitude = Length.meters(10.0)
                    ),
                    ExerciseRoute.Location(
                        time = Instant.now().plusSeconds(3600),
                        latitude = 0.0,
                        longitude = 0.0,
                        horizontalAccuracy = Length.meters(10.0),
                        verticalAccuracy = Length.meters(10.0),
                        altitude = Length.meters(10.0)
                    )
                )
            ),
            speedRecord = listOf(
                SpeedRecord.Sample(
                    time = Instant.now(),
                    speed = Velocity.metersPerSecond(10.0)
                ),
                SpeedRecord.Sample(
                    time = Instant.now().plusSeconds(3600),
                    speed = Velocity.metersPerSecond(10.0)
                )

            )
        )

        checkSerialization(runData)

    }


    /**
     * Funzione per verificare la serializzazione e deserializzazione di un oggetto
     *
     * @param obj oggetto da verificare
     * @return true se la serializzazione e deserializzazione sono andate a buon fine, false altrimenti
     */
    private inline fun <reified T> checkSerialization(obj: T): Boolean {
        try {
            if (obj == Json.decodeFromString<T>(Json.encodeToString(obj))) {
                Log.d("Serializing", "Serializzazione e deserializzazione riuscite")
                return true
            } else {
                Log.e(
                    "Serializing",
                    "Gli oggetti serializzati e deserializzati non sono uguali"
                )
                return false
            }
        } catch (e: Exception) {
            // Log dell'errore nel caso in cui si verifichi un'eccezione
            Log.e(
                "Serializing",
                "Errore durante la serializzazione/deserializzazione: ${e.message}"
            )
            return false
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
