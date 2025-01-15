package com.lam.pedro.presentation.serialization

import android.os.Environment
import android.util.Log
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.BasicActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.lists.SpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.primitive.ExerciseSegmentSerializer
import com.lam.pedro.presentation.serialization.SessionCreator.createCyclingSession
import com.lam.pedro.presentation.serialization.SessionCreator.createDriveSession
import com.lam.pedro.presentation.serialization.SessionCreator.createLiftSession
import com.lam.pedro.presentation.serialization.SessionCreator.createListenSession
import com.lam.pedro.presentation.serialization.SessionCreator.createRunSession
import com.lam.pedro.presentation.serialization.SessionCreator.createSitSession
import com.lam.pedro.presentation.serialization.SessionCreator.createSleepSession
import com.lam.pedro.presentation.serialization.SessionCreator.createTrainSession
import com.lam.pedro.presentation.serialization.SessionCreator.createUnknownSession
import com.lam.pedro.presentation.serialization.SessionCreator.createWalkSession
import com.lam.pedro.presentation.serialization.SessionCreator.createYogaSession
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

object JsonDBManager {
    private fun extractDouble(jsonObject: JsonObject, key: String): Double =
        jsonObject[key]?.jsonPrimitive?.doubleOrNull ?: 0.0

    private fun extractLong(jsonObject: JsonObject): Long =
        jsonObject["stepsCount"]?.jsonPrimitive?.long ?: 0L

    private val activityCreatorsJSON = mapOf<ActivityEnum, (BasicActivity, JsonObject?) ->
    GenericActivity>(
        ActivityEnum.CYCLING to { basicActivity, jsonData ->
            createCyclingSession(
                basicActivity = basicActivity,
                distance = Length.meters(extractDouble(jsonData!!, "distance")),
                totalEnergy = Energy.calories(extractDouble(jsonData, "totalEnergy")),
                activeEnergy = Energy.calories(extractDouble(jsonData, "activeEnergy")),
                speedSamples = jsonData["speedSamples"]?.let {
                    Json.decodeFromJsonElement(
                        ListSerializer(SpeedRecordSampleSerializer),
                        it
                    )
                }!!,
                exerciseRoute = jsonData["exerciseRoute"]?.let {
                    Json.decodeFromJsonElement(ExerciseRouteSerializer, it)
                }!!
            )
        },
        ActivityEnum.RUN to { basicActivity, jsonData ->
            createRunSession(
                basicActivity = basicActivity,
                stepsCount = extractLong(jsonData!!),
                totalEnergy = Energy.calories(extractDouble(jsonData, "totalEnergy")),
                activeEnergy = Energy.calories(extractDouble(jsonData, "activeEnergy")),
                distance = Length.meters(extractDouble(jsonData, "distance")),
                exerciseRoute = jsonData["exerciseRoute"]?.let {
                    Json.decodeFromJsonElement(ExerciseRouteSerializer, it)
                }!!,
                speedSamples = jsonData["speedSamples"]?.let {
                    Json.decodeFromJsonElement(
                        ListSerializer(SpeedRecordSampleSerializer),
                        it
                    )
                }!!
            )
        },
        ActivityEnum.WALK to { basicActivity, jsonData ->
            createWalkSession(
                basicActivity = basicActivity,
                stepsCount = extractLong(jsonData!!),
                totalEnergy = Energy.calories(extractDouble(jsonData, "totalEnergy")),
                activeEnergy = Energy.calories(extractDouble(jsonData, "activeEnergy")),
                distance = Length.meters(extractDouble(jsonData, "distance")),
                exerciseRoute = jsonData["exerciseRoute"]?.let {
                    Json.decodeFromJsonElement(ExerciseRouteSerializer, it)
                }!!,
                speedSamples = jsonData["speedSamples"]?.let {
                    Json.decodeFromJsonElement(
                        ListSerializer(SpeedRecordSampleSerializer),
                        it
                    )
                } ?: emptyList()
            )
        },
        ActivityEnum.SIT to { basicActivity, jsonData ->
            createSitSession(
                basicActivity = basicActivity,
                volume = Volume.liters(extractDouble(jsonData!!, "volume"))
            )
        },
        ActivityEnum.YOGA to { basicActivity, jsonData ->
            createYogaSession(
                basicActivity = basicActivity,
                totalEnergy = Energy.calories(extractDouble(jsonData!!, "totalEnergy")),
                activeEnergy = Energy.calories(extractDouble(jsonData, "activeEnergy")),
                exerciseSegment = jsonData["exerciseSegment"]?.let {
                    Json.decodeFromJsonElement(ListSerializer(ExerciseSegmentSerializer), it)
                } ?: emptyList(),
                exerciseLap = jsonData["exerciseLap"]?.let {
                    Json.decodeFromJsonElement(ListSerializer(ExerciseLapSerializer), it)
                } ?: emptyList()
            )
        },

        ActivityEnum.TRAIN to { basicActivity, jsonData ->
            createTrainSession(
                basicActivity = basicActivity,
                totalEnergy = Energy.calories(extractDouble(jsonData!!, "totalEnergy")),
                activeEnergy = Energy.calories(extractDouble(jsonData, "activeEnergy")),
                exerciseSegment = jsonData["exerciseSegment"]?.let {
                    Json.decodeFromJsonElement(ListSerializer(ExerciseSegmentSerializer), it)
                } ?: emptyList(),
                exerciseLap = jsonData["exerciseLap"]?.let {
                    Json.decodeFromJsonElement(ListSerializer(ExerciseLapSerializer), it)
                } ?: emptyList()
            )
        },

        ActivityEnum.DRIVE to { basicActivity, jsonData ->
            createDriveSession(
                basicActivity = basicActivity,
                distance = Length.meters(extractDouble(jsonData!!, "distance")),
                exerciseRoute = jsonData["exerciseRoute"]?.let {
                    Json.decodeFromJsonElement(ExerciseRouteSerializer, it)
                }!!,
                speedSamples = jsonData["speedSamples"]?.let {
                    Json.decodeFromJsonElement(
                        ListSerializer(SpeedRecordSampleSerializer),
                        it
                    )
                } ?: emptyList()
            )
        },

        ActivityEnum.LIFT to { basicActivity, jsonData ->
            createLiftSession(
                basicActivity = basicActivity,
                totalEnergy = Energy.calories(extractDouble(jsonData!!, "totalEnergy")),
                activeEnergy = Energy.calories(extractDouble(jsonData, "activeEnergy")),
                exerciseSegment = jsonData["exerciseSegment"]?.let {
                    Json.decodeFromJsonElement(ListSerializer(ExerciseSegmentSerializer), it)
                } ?: emptyList(),
                exerciseLap = jsonData["exerciseLap"]?.let {
                    Json.decodeFromJsonElement(ListSerializer(ExerciseLapSerializer), it)
                } ?: emptyList()
            )
        },

        ActivityEnum.SLEEP to { basicActivity, _ ->
            createSleepSession(
                basicActivity = basicActivity
            )
        },

        ActivityEnum.LISTEN to { basicActivity, _ ->
            createListenSession(
                basicActivity = basicActivity
            )
        },
        
        ActivityEnum.UNKNOWN to { basicActivity, _ ->
            createUnknownSession(
                basicActivity = basicActivity
            )
        }

    )

    fun saveExportedJSON(jsonContent: String): Boolean {
        val fileName = "activities" + ".json"
        return try {
            // Ottieni la directory pubblica dei download
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Crea la cartella PEDRO se non esiste
            val pedroDir = File(downloadsDir, "PEDRO")
            if (!pedroDir.exists()) {
                pedroDir.mkdir()  // Crea la cartella PEDRO
            }

            // Imposta il percorso del file
            var file = File(pedroDir, fileName)

            // Aggiungi suffissi incrementali se il file esiste già
            if (file.exists()) {
                var counter = 1
                val nameWithoutExtension = fileName.substringBeforeLast(".")
                val extension = fileName.substringAfterLast(".", "")
                while (file.exists()) {
                    val newName = if (extension.isNotEmpty()) {
                        "$nameWithoutExtension-$counter.$extension"
                    } else {
                        "$nameWithoutExtension-$counter"
                    }
                    file = File(pedroDir, newName)
                    counter++
                }
            }

            // Scrive il contenuto nel file con il nome unico
            FileOutputStream(file).use { output ->
                output.write(jsonContent.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Converte un oggetto JSON in un'attività generica
     */
    private fun jsonToActivityHelper(jsonObject: JsonObject): GenericActivity? {
        val basicActivity = SessionCreator.createBasicActivity(
            startTime = parseToInstant(jsonObject, "start_time"),
            endTime = parseToInstant(jsonObject, "end_time"),
            notes = jsonObject["notes"]?.jsonPrimitive?.content ?: "",
            title = jsonObject["title"]?.jsonPrimitive?.content ?: ""
        )
        val activityEnum = ActivityEnum.valueOf(
            jsonObject["activity_type"]?.jsonPrimitive?.content ?: return null
        )

        val jsonData = jsonObject["JSON"] as? JsonObject

        return activityCreatorsJSON[activityEnum]?.let { creator ->
            return creator(basicActivity, jsonData)
        }
    }

    private fun parseToInstant(jsonObject: JsonObject, key: String): Instant {
        val wrong = "+00:00:00"
        val right = "+00:00"
        return Instant.parse(jsonObject[key]!!.jsonPrimitive.content.replace(wrong, right))
    }

    fun fromJsonListToGenericActivityList(json: List<JsonObject>): List<GenericActivity> {
        val genericActivities = mutableListOf<GenericActivity>()
        json.forEach {
            if (it["user_id"]?.jsonPrimitive?.content != getUUID()) {
                Log.e("JSON", "Utente non autorizzato")
                return@forEach
            }
            val activity: GenericActivity? = jsonToActivityHelper(it)
            if (activity != null) {
                Log.d("JSON", "Attività convertita con successo: $activity")
                genericActivities.add(activity)
            } else {
                Log.e("JSON", "Errore durante la conversione dell'attività")
            }
        }
        return genericActivities
    }
}