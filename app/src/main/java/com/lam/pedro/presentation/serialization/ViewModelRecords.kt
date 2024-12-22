package com.lam.pedro.presentation.serialization

import android.util.Log
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.safeRpcCall
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.lists.SpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.primitive.ExerciseSegmentSerializer
import com.lam.pedro.presentation.serialization.SessionCreator.activityConfigs
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import java.time.Instant

class ViewModelRecords : ViewModel() {

    val tag = "ViewModelRecords-Supabase"

    @OptIn(InternalSerializationApi::class)
    suspend fun getActivitySession(
        activityEnum: ActivityEnum
    ): List<GenericActivity> {
        Log.d(tag, "Recupero delle attività di tipo $activityEnum")
        val config = activityConfigs[activityEnum] as? SessionCreator.ActivityConfig<*>


        if (config != null) {
            return withContext(Dispatchers.IO) {
                val uuid = getUUID()
                try {
                    val jsonFinal =
                        buildJsonObject { put("user_uuid", Json.encodeToJsonElement(uuid)) }
                    val responseJson = (
                            safeRpcCall(
                                rpcFunctionName = "get_${activityEnum.name.lowercase()}_session",
                                jsonFinal = jsonFinal
                            ) as? PostgrestResult
                            )?.data

                    val response: List<GenericActivity> = Json.decodeFromString(
                        ListSerializer(config.responseType.serializer()),
                        responseJson!!
                    )
                    response
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

    fun insertActivitySession(genericActivities: List<GenericActivity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = getUUID().toString()

            genericActivities.forEach { activity ->
                try {
                    Log.d(
                        tag,
                        "Preparazione dell'inserimento per l'attività: ${activity.activityEnum.name}"
                    )

                    val jsonPayload = createActivityJson(activity, uuid)
                    Log.d(tag, "Payload JSON generato: $jsonPayload")

                    // Inserisci la sessione nel database
                    safeRpcCall(
                        rpcFunctionName = "insert_${activity.activityEnum.name.lowercase()}_session",
                        jsonFinal = jsonPayload
                    )

                    Log.d(
                        tag, "Sessione inserita con successo: ${activity.activityEnum.name}"
                    )
                } catch (e: Exception) {
                    Log.e(
                        tag,
                        "Errore durante l'inserimento della sessione: ${activity.activityEnum.name}",
                        e
                    )
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

    private fun parseToInstant(input: String): Instant {
        // Corregge il formato rimuovendo il secondo offset ":00"
        val correctedInput = input.replace("+00:00:00", "+00:00")
        return Instant.parse(correctedInput)
    }


    fun dumpActivitiesFromDB(): List<GenericActivity> {
        val genericActivities = mutableListOf<GenericActivity>()
        viewModelScope.launch(Dispatchers.IO) {

            val json = supabase()
                .from("activity")
                .select()
                .decodeList<JsonObject>()

            Log.d(tag, "Attività recuperate con successo $json")


            json.forEach {
                val activity: GenericActivity? = fromJsonToActivity(it)
                if (activity != null) {
                    Log.d(tag, "Attività convertita con successo: $activity")
                    genericActivities.add(activity)
                } else {
                    Log.e(tag, "Errore durante la conversione dell'attività")
                }
            }
        }

        return emptyList()
    }

    private fun fromJsonToActivity(jsonObject: JsonObject): GenericActivity? {


        var distance = Length.meters(0.0)
        var totalEnergy = Energy.calories(0.0)
        var activeEnergy = Energy.calories(0.0)
        var speedSamples = emptyList<SpeedRecord.Sample>()
        var stepsCount = 0L
        var volume = Volume.liters(0.0)
        var exerciseLap = emptyList<ExerciseLap>()
        var exerciseSegment = emptyList<ExerciseSegment>()
        var exerciseRoute: ExerciseRoute? = null


        // Verifica che JSON sia un JsonObject
        val jsonData = jsonObject["JSON"] as? JsonObject
        if (jsonData != null) {
            distance = Length.meters(
                jsonData["distance"]?.jsonPrimitive?.double ?: 0.0
            )
            totalEnergy = Energy.calories(
                jsonData["totalEnergy"]?.jsonPrimitive?.double ?: 0.0
            )
            activeEnergy = Energy.calories(
                jsonData["activeEnergy"]?.jsonPrimitive?.double ?: 0.0
            )
            speedSamples = jsonData["speedSamples"]?.let {
                Json.decodeFromJsonElement(
                    ListSerializer(SpeedRecordSampleSerializer),
                    it
                )
            } ?: emptyList()

            stepsCount = jsonData["stepsCount"]?.jsonPrimitive?.long ?: 0L
            volume = Volume.liters(
                jsonData["volume"]?.jsonPrimitive?.double ?: 0.0
            )

            exerciseLap = jsonData["exerciseLap"]?.let {
                Json.decodeFromJsonElement(ListSerializer(ExerciseLapSerializer), it)
            } ?: emptyList()

            exerciseSegment = jsonData["exerciseSegment"]?.let {
                Json.decodeFromJsonElement(ListSerializer(ExerciseSegmentSerializer), it)
            } ?: emptyList()

            exerciseRoute = jsonData["exerciseRoute"]?.let {
                Json.decodeFromJsonElement(ExerciseRouteSerializer, it)
            }
        } else {
            Log.w(
                "ActivityParser",
                "Il campo JSON è null o non è un JsonObject per l'attività con ID: ${jsonObject["id"]}"
            )
        }
        val activityEnum = ActivityEnum.valueOf(
            jsonObject["activity_type"]?.jsonPrimitive?.content ?: return null
        )

        val basicActivity = SessionCreator.createBasicActivity(
            startTime = parseToInstant(jsonObject["start_time"]!!.jsonPrimitive.content),
            endTime = parseToInstant(jsonObject["end_time"]!!.jsonPrimitive.content),
            notes = jsonObject["notes"]?.jsonPrimitive?.content ?: "",
            title = jsonObject["title"]?.jsonPrimitive?.content ?: ""
        )

        try {
            return when (activityEnum) {
                ActivityEnum.CYCLING ->
                    SessionCreator.createCyclingSession(
                        basicActivity = basicActivity,
                        distance = distance,
                        totalEnergy = totalEnergy,
                        activeEnergy = activeEnergy,
                        speedSamples = speedSamples,
                        exerciseRoute = exerciseRoute!!
                    )

                ActivityEnum.RUN ->
                    SessionCreator.createRunSession(
                        basicActivity = basicActivity,
                        speedSamples = speedSamples,
                        stepsCount = stepsCount,
                        totalEnergy = totalEnergy,
                        activeEnergy = activeEnergy,
                        distance = distance,
                        exerciseRoute = exerciseRoute!!
                    )

                ActivityEnum.WALK ->
                    SessionCreator.createWalkSession(
                        basicActivity = basicActivity,
                        speedSamples = speedSamples,
                        stepsCount = stepsCount,
                        totalEnergy = totalEnergy,
                        activeEnergy = activeEnergy,
                        distance = distance,
                        exerciseRoute = exerciseRoute!!
                    )

                ActivityEnum.SIT ->
                    SessionCreator.createSitSession(
                        basicActivity = basicActivity,
                        volume = volume
                    )

                ActivityEnum.YOGA ->
                    SessionCreator.createYogaSession(
                        basicActivity = basicActivity,
                        totalEnergy = totalEnergy,
                        activeEnergy = activeEnergy,
                        exerciseSegment = exerciseSegment,
                        exerciseLap = exerciseLap
                    )

                ActivityEnum.TRAIN ->
                    SessionCreator.createTrainSession(
                        basicActivity = basicActivity,
                        totalEnergy = totalEnergy,
                        activeEnergy = activeEnergy,
                        exerciseSegment = exerciseSegment,
                        exerciseLap = exerciseLap
                    )

                ActivityEnum.DRIVE ->
                    SessionCreator.createDriveSession(
                        basicActivity = basicActivity,
                        distance = distance,
                        exerciseRoute = exerciseRoute!!,
                        speedSamples = speedSamples
                    )

                ActivityEnum.LIFT ->
                    SessionCreator.createLiftSession(
                        basicActivity = basicActivity,
                        totalEnergy = totalEnergy,
                        activeEnergy = activeEnergy,
                        exerciseSegment = exerciseSegment,
                        exerciseLap = exerciseLap
                    )

                ActivityEnum.SLEEP ->
                    SessionCreator.createSleepSession(
                        basicActivity = basicActivity
                    )

                ActivityEnum.LISTEN ->
                    SessionCreator.createListenSession(
                        basicActivity = basicActivity
                    )
            }
        } catch (e: Exception) {
            Log.e(tag, "Errore durante la conversione del JSON in attività $activityEnum : $e")
            return null
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


