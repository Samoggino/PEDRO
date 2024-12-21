package com.lam.pedro.presentation.serialization

import android.util.Log
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
import kotlinx.serialization.json.jsonObject
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

        val basicActivity = SessionCreator.createBasicActivity(
            startTime = parseToInstant(
                jsonObject["start_time"]?.jsonPrimitive?.content ?: return null
            ),
            endTime = parseToInstant(jsonObject["end_time"]?.jsonPrimitive?.content ?: return null),
            notes = jsonObject["notes"]?.jsonPrimitive?.content ?: "",
            title = jsonObject["title"]?.jsonPrimitive?.content ?: ""
        )

        /**
         * FIXME: le parti particolari delle attività sono sotto JSON,
         * ad esempio oggetto{"JSON" :{ "distance": 1000.0, "activeEnergy": 100.0}}
         */
//        val distance = Length.meters(jsonObject["distance"]?.jsonPrimitive?.double ?: 0.0)

        val distance = Length.meters(
            jsonObject["JSON"]?.jsonObject?.get("distance")?.jsonPrimitive?.double ?: 0.0
        )

        val totalEnergy = Energy.calories(
            jsonObject["JSON"]?.jsonObject?.get("totalEnergy")?.jsonPrimitive?.double ?: 0.0
        )
        val activeEnergy = Energy.calories(
            jsonObject["JSON"]?.jsonObject?.get("activeEnergy")?.jsonPrimitive?.double ?: 0.0
        )
        val speedSamples = jsonObject["JSON"]?.jsonObject?.get("speedSamples")?.let {
            Json.decodeFromJsonElement(
                ListSerializer(SpeedRecordSampleSerializer),
                it
            )
        } ?: emptyList()

        val stepsCount =
            jsonObject["JSON"]?.jsonObject?.get("stepsCount")?.jsonPrimitive?.long ?: 0L
        val volume = Volume.liters(
            jsonObject["JSON"]?.jsonObject?.get("volume")?.jsonPrimitive?.double ?: 0.0
        )

        val exerciseLap = jsonObject["JSON"]?.jsonObject?.get("exerciseLap")?.let {
            Json.decodeFromJsonElement(ListSerializer(ExerciseLapSerializer), it)
        } ?: emptyList()

        val exerciseSegment = jsonObject["JSON"]?.jsonObject?.get("exerciseSegment")?.let {
            Json.decodeFromJsonElement(ListSerializer(ExerciseSegmentSerializer), it)
        } ?: emptyList()

        val exerciseRoute = jsonObject["JSON"]?.jsonObject?.get("exerciseRoute")?.let {
            Json.decodeFromJsonElement(ExerciseRouteSerializer, it)
        }


        val activityEnum = ActivityEnum.valueOf(
            jsonObject["activity_type"]?.jsonPrimitive?.content ?: return null
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
                    /**
                     * FIXME: mi dà errore sleep, forse perchè non ha parametri
                     */
                    SessionCreator.createSleepSession(
                        basicActivity = basicActivity
                    )

                ActivityEnum.LISTEN ->

                    /**
                     * FIXME: mi dà errore listen, forse perchè non ha parametri,
                     * però non capisco perchè dovrebbe dare errori.
                     * java.lang.IllegalArgumentException: Element class kotlinx.serialization.json.JsonNull is not a JsonObject
                     *
                     */

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


