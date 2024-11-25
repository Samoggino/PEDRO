package com.lam.pedro.presentation.serialization.activecalories

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.BasicActivity
import com.lam.pedro.data.activity.CyclingSession
import com.lam.pedro.data.activity.DriveSession
import com.lam.pedro.data.activity.LiftSession
import com.lam.pedro.data.activity.RunSession
import com.lam.pedro.data.activity.TrainSession
import com.lam.pedro.data.activity.YogaSession
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import java.time.Instant
import java.time.ZoneOffset

class ViewModelRecords : ViewModel() {

    val startTime: Instant = Instant.now()
    val endTime: Instant = Instant.now().plusSeconds(3600)
    val startZoneOffset: ZoneOffset? = ZoneOffset.UTC
    val endZoneOffset: ZoneOffset? = ZoneOffset.UTC
    val energy: Energy = Energy.kilocalories(100.0)
    val length: Length = Length.meters(500.0)


    suspend fun getActivitySession(context: Context, activityType: ActivityType) {
        try {
            val response = when (activityType) {
                ActivityType.YOGA -> {
                    supabase()
                        .postgrest
                        .rpc("get_yoga_session", buildJsonObject {
                            put("user_uuid", getUUID(context).toString())
                        })
                        .decodeList<YogaSession>()
                }

                ActivityType.RUN -> {
                    supabase()
                        .postgrest
                        .rpc("get_run_session", buildJsonObject {
                            put("user_uuid", getUUID(context).toString())
                        })
                        .decodeList<RunSession>()
                }

                ActivityType.CYCLING -> {

                    supabase()
                        .postgrest
                        .rpc("get_cycling_session", buildJsonObject {
                            put("user_uuid", getUUID(context).toString())
                        })
                        .decodeList<CyclingSession>()
                }

                ActivityType.TRAIN -> {
                    supabase()
                        .postgrest
                        .rpc("get_train_session", buildJsonObject {
                            put("user_uuid", getUUID(context).toString())
                        })
                        .decodeList<TrainSession>()
                }

                ActivityType.DRIVE -> {
                    supabase()
                        .postgrest
                        .rpc("get_drive_session", buildJsonObject {
                            put("user_uuid", getUUID(context).toString())
                        })
                        .decodeList<DriveSession>()
                }

                ActivityType.SIT -> TODO()
                ActivityType.SLEEP -> TODO()
                ActivityType.WALK -> TODO()
                ActivityType.LIFT -> {
                    supabase()
                        .postgrest
                        .rpc("get_lift_session", buildJsonObject {
                            put("user_uuid", getUUID(context).toString())
                        })
                        .decodeList<LiftSession>()
                }
            }

            Log.d("Supabase", "Dati delle attività: $response")

        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore durante il recupero dei dati delle attività $e"
            )
        }
    }

    suspend fun insertActivitySession(context: Context, activityType: ActivityType) {
        try {
            val supabase = supabase()
            when (activityType) {
                ActivityType.YOGA -> {
                    Log.d("Supabase", "Creazione di una sessione di yoga")
                    val yogaSession = YogaSession(
                        basicActivity = BasicActivity(
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

                }

                ActivityType.RUN -> {
                    Log.d("Supabase", "Creazione di una sessione di corsa")
                    val runSession = RunSession(
                        basicActivity = BasicActivity(
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

                    val jsonFinal = buildJsonObject {
                        put("input_data", buildJsonObject {
                            put("data", Json.encodeToJsonElement(runSession))
                            put("user_UUID", Json.encodeToJsonElement(getUUID(context)))
                        })
                    }

                    // Esegui la chiamata RPC su Supabase
                    supabase.postgrest.rpc("insert_run_session", jsonFinal)
                }

                ActivityType.CYCLING -> {
                    Log.d("Supabase", "Creazione di una sessione di ciclismo")
                    val cyclingSession = CyclingSession(
                        basicActivity = BasicActivity(
                            startTime = Instant.now(),
                            endTime = Instant.now(),
                            title = "Cycling",
                            notes = "Cycling session"
                        ),
                        totalEnergy = energy,
                        activeEnergy = energy,
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
                                speed = Velocity.metersPerSecond(10.0)
                            ),
                            SpeedRecord.Sample(
                                time = endTime,
                                speed = Velocity.metersPerSecond(10.0)
                            )
                        ),
                        cyclingPedalingCadenceSamples = listOf(
                            CyclingPedalingCadenceRecord.Sample(
                                time = Instant.now(),
                                revolutionsPerMinute = 1.0
                            ),
                            CyclingPedalingCadenceRecord.Sample(
                                time = endTime,
                                revolutionsPerMinute = 1.0
                            )
                        )
                    )

                    val jsonFinal = buildJsonObject {
                        put("input_data", buildJsonObject {
                            put("data", Json.encodeToJsonElement(cyclingSession))
                            put("user_UUID", Json.encodeToJsonElement(getUUID(context)))
                        })
                    }

                    // Esegui la chiamata RPC su Supabase
                    supabase.postgrest.rpc("insert_cycling_session", jsonFinal)
                }

                ActivityType.TRAIN -> {
                    Log.d("Supabase", "Creazione di una sessione di allenamento")
                    val trainSession = TrainSession(
                        basicActivity = BasicActivity(
                            startTime = Instant.now(),
                            endTime = Instant.now(),
                            title = "Train",
                            notes = "Train session"
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
                            put("data", Json.encodeToJsonElement(trainSession))
                            put("user_UUID", Json.encodeToJsonElement(getUUID(context)))
                        })
                    }

                    // Esegui la chiamata RPC su Supabase
                    supabase.postgrest.rpc("insert_train_session", jsonFinal)
                }

                ActivityType.DRIVE -> {
                    Log.d("Supabase", "Creazione di una sessione di guida")
                    val driveSession = DriveSession(
                        basicActivity = BasicActivity(
                            startTime = Instant.now(),
                            endTime = Instant.now(),
                            title = "Drive",
                            notes = "Drive session"
                        ),
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
                        )
                    )

                    val jsonFinal = buildJsonObject {
                        put("input_data", buildJsonObject {
                            put("data", Json.encodeToJsonElement(driveSession))
                            put("user_UUID", Json.encodeToJsonElement(getUUID(context)))
                        })
                    }

                    // Esegui la chiamata RPC su Supabase
                    supabase.postgrest.rpc("insert_drive_session", jsonFinal)
                }

                ActivityType.SIT -> TODO()
                ActivityType.SLEEP -> TODO()
                ActivityType.WALK -> TODO()
                ActivityType.LIFT -> {
                    Log.d("Supabase", "Creazione di una sessione di sollevamento pesi")
                    val liftSession = LiftSession(
                        basicActivity = BasicActivity(
                            startTime = Instant.now(),
                            endTime = Instant.now(),
                            title = "Lift",
                            notes = "Lift session"
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
                            put("data", Json.encodeToJsonElement(liftSession))
                            put("user_UUID", Json.encodeToJsonElement(getUUID(context)))
                        })
                    }

                    // Esegui la chiamata RPC su Supabase
                    supabase.postgrest.rpc("insert_lift_session", jsonFinal)
                }
            }


        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore durante il recupero dei dati delle attività $e"
            )
        }
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
