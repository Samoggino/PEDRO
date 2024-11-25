@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class,
    InstantSerializer::class
)

package com.lam.pedro.presentation.serialization.activecalories

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.activity.ActivitySession
import com.lam.pedro.data.activity.CyclingData
import com.lam.pedro.data.activity.RunSession
import com.lam.pedro.data.activity.TrainData
import com.lam.pedro.data.activity.YogaSession
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class ViewModelRecords : ViewModel() {

    val startTime: Instant = Instant.now()
    val endTime: Instant = Instant.now().plusSeconds(3600)
    val startZoneOffset: ZoneOffset? = ZoneOffset.UTC
    val endZoneOffset: ZoneOffset? = ZoneOffset.UTC
    val energy: Energy = Energy.kilocalories(100.0)
    val length: Length = Length.meters(500.0)


    /**
     * TrainData
     */
    fun actionOne() {

        try {
            val trainData = TrainData(
                activeCaloriesBurnedRecord = ActiveCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    energy = energy
                ),
                repetitionsGoal = ExerciseCompletionGoal.RepetitionsGoal(
                    repetitions = 10
                ),
                durationGoal = ExerciseCompletionGoal.DurationGoal(
                    duration = Duration.between(
                        startTime,
                        endTime
                    )
                ),
                exerciseLap = ExerciseLap(
                    startTime = startTime,
                    endTime = endTime,
                    length = length
                ),
                totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    energy = energy
                )
            )

            checkSerialization(trainData)

        } catch (e: Exception) {
            Log.e(
                "Supabase",
                "Errore durante il caricamento dei dati: ${e.message}",
            )
        }

    }

    /**
     * CyclingData
     */
    fun actionTwo() {

        try {
            val cyclingData = CyclingData(
                calories = ActiveCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    energy = energy
                ),

                distanceRecord = DistanceRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    distance = length
                ),
                elevationGainedRecord = ElevationGainedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    elevation = length
                ),
                exerciseRoute = ExerciseRoute(
                    route = listOf(
                        ExerciseRoute.Location(
                            time = startTime,
                            latitude = 0.0,
                            longitude = 0.0,
                            horizontalAccuracy = length,
                            verticalAccuracy = length,
                            altitude = length
                        ),
                        ExerciseRoute.Location(
                            time = endTime,
                            latitude = 0.0,
                            longitude = 0.0,
                            horizontalAccuracy = length,
                            verticalAccuracy = length,
                            altitude = length
                        )
                    )
                ),
                pedalingCadenceRecord = CyclingPedalingCadenceRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    samples = listOf(
                        CyclingPedalingCadenceRecord.Sample(
                            time = startTime,
                            revolutionsPerMinute = 10.0
                        ),
                        CyclingPedalingCadenceRecord.Sample(
                            time = endTime,
                            revolutionsPerMinute = 10.0
                        )
                    )
                ),
                speedRecord = SpeedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    samples = listOf(
                        SpeedRecord.Sample(
                            time = Instant.now(),
                            speed = Velocity.kilometersPerHour(10.0)
                        ),
                        SpeedRecord.Sample(
                            time = endTime,
                            speed = Velocity.kilometersPerHour(10.0)
                        )
                    )
                ),
                totalCaloriesBurned = TotalCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    energy = energy
                )
            )

            checkSerialization(cyclingData)
        } catch (e: Exception) {
            Log.e("Serializing", "Errore durante il caricamento dei dati: ${e.message}")
        }

    }


    /**
     * YogaData
     */
    suspend fun yogaSession(context: Context) {
        try {
            Log.d("Supabase", "Creazione di una sessione di yoga")
            val supabase = supabase()

            val yogaSession = YogaSession(
                activitySession = ActivitySession(
                    startTime = Instant.now(),
                    endTime = Instant.now(),
                    title = "Yoga",
                    notes = "Yoga session"
                ),
                totalEnergy = energy,
                activeEnergy = energy,
                exerciseSegment = listOf(
                    ExerciseSegment(
                        startTime = Instant.now(),
                        endTime = Instant.now(),
                        segmentType = 0,
                        repetitions = 1
                    )
                ),
                exerciseLap = listOf(
                    ExerciseLap(
                        startTime = Instant.now(),
                        endTime = Instant.now(),
                        length = length
                    )
                )
            )


            val jsonFinal = buildJsonObject {
                put("input_data", buildJsonObject {
                    put("data", Json.encodeToJsonElement(yogaSession))
                    put("user_UUID", Json.encodeToJsonElement(getUUID(context)))
                })
            }

            // Esegui la chiamata RPC su Supabase
            supabase.postgrest.rpc("insert_yoga_session", jsonFinal)


        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la creazione di YogaRecord: ${e.message}")
        }
    }

    suspend fun getYogaSession(context: Context) {
        try {
            val response = supabase()
                .postgrest
                .rpc("get_yoga_session", buildJsonObject {
                    put("user_uuid", getUUID(context).toString())
                })
                .decodeList<YogaSession>()


            Log.d("Supabase", "Dati delle sessioni di yoga: $response")

        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore durante il recupero dei dati di sonno $e"
            )
        }
    }


    fun actionFour() {
        // crea un oggetto RunData e prova a serializzarlo e deserializzarlo
        val runData = RunSession(
            activitySession = ActivitySession(
                startTime = Instant.now(),
                endTime = Instant.now(),
                title = "Run",
                notes = "Running session"
            ),
            activeEnergy = energy,
            totalEnergy = energy,
            distance = length,
            elevationGained = length,
            exerciseRoute = ExerciseRoute(
                route = listOf(
                    ExerciseRoute.Location(
                        time = startTime,
                        latitude = 0.0,
                        longitude = 0.0,
                        horizontalAccuracy = length,
                        verticalAccuracy = length,
                        altitude = length
                    ),
                    ExerciseRoute.Location(
                        time = endTime,
                        latitude = 0.0,
                        longitude = 0.0,
                        horizontalAccuracy = length,
                        verticalAccuracy = length,
                        altitude = length
                    )
                )
            ),
            speedSamples = listOf(
                SpeedRecord.Sample(
                    time = Instant.now(),
                    speed = Velocity.kilometersPerHour(10.0)
                ),
                SpeedRecord.Sample(
                    time = endTime,
                    speed = Velocity.kilometersPerHour(10.0)
                )
            ),
            stepsCount = 1000
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

            val json = Json.encodeToString(obj)

            Log.d("Serializing", "Oggetto serializzato: $json")

            if (obj == Json.decodeFromString<T>(json)) {
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
