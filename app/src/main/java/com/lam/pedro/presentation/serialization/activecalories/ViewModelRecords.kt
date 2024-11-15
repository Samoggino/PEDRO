@file:UseSerializers(
    ActiveCaloriesBurnedRecordSerializer::class,
    InstantSerializer::class
)

package com.lam.pedro.presentation.serialization.activecalories

import android.util.Log
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseCompletionGoal
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.activity.CyclingData
import com.lam.pedro.data.activity.RunData
import com.lam.pedro.data.activity.TrainData
import com.lam.pedro.data.activity.YogaData
import com.lam.pedro.data.serializers.activity.ActiveCaloriesBurnedRecordSerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    fun actionThree() {


        // Creazione di un oggetto YogaRecord con i valori di esempio

        try {
            val yogaData = YogaData(
                // crea un oggetto YogaRecord e prova a serializzarlo e deserializzarlo
                // Creazione di un oggetto ActiveCaloriesBurnedRecord con valori di esempio
                calories = ActiveCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime, // 1 ora di durata
                    endZoneOffset = endZoneOffset,
                    energy = energy // Ad esempio, 100 kcal
                ),

                // Creazione di un oggetto ExerciseCompletionGoal.DurationGoal con valore di esempio
                durationGoal = ExerciseCompletionGoal.DurationGoal(
                    duration = Duration.between(
                        startTime, endTime
                    )
                ), // Durata di 1 ora

                // Creazione di un oggetto TotalCaloriesBurnedRecord con valori di esempio
                totalCaloriesBurned = TotalCaloriesBurnedRecord(
                    startTime = startTime,
                    startZoneOffset = startZoneOffset,
                    endTime = endTime,
                    endZoneOffset = endZoneOffset,
                    energy = energy
                ),

                // Creazione di un oggetto ExerciseLap con valori di esempio
                exerciseLap = ExerciseLap(
                    startTime = startTime,
                    endTime = endTime,
                    length = length// Ad esempio, una distanza di 500 metri
                )
            )

            checkSerialization(yogaData)

        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la creazione di YogaRecord: ${e.message}")
        }
    }


    fun actionFour() {
        // crea un oggetto RunData e prova a serializzarlo e deserializzarlo
        val runData = RunData(
            calories = ActiveCaloriesBurnedRecord(
                startTime = startTime,
                startZoneOffset = startZoneOffset,
                endTime = endTime,
                endZoneOffset = endZoneOffset,
                energy = energy
            ),
            totalCaloriesBurned = TotalCaloriesBurnedRecord(
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
            cadenceRecord = listOf(
                StepsCadenceRecord.Sample(
                    time = Instant.now(),
                    rate = 10.0
                ),
                StepsCadenceRecord.Sample(
                    time = endTime,
                    rate = 10.0
                )
            ),
            stepsRecord = StepsRecord(
                startTime = startTime,
                startZoneOffset = startZoneOffset,
                endTime = endTime,
                endZoneOffset = endZoneOffset,
                count = 1000
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
