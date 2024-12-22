package com.lam.pedro.presentation.serialization

import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.BasicActivity
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
import com.lam.pedro.presentation.serialization.SessionCreator.createWalkSession
import com.lam.pedro.presentation.serialization.SessionCreator.createYogaSession
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

object JsonDBManager {
    private fun extractDouble(jsonObject: JsonObject, key: String): Double =
        jsonObject[key]?.jsonPrimitive?.doubleOrNull ?: 0.0

    private fun extractLong(jsonObject: JsonObject, key: String): Long =
        jsonObject[key]?.jsonPrimitive?.long ?: 0L


    val activityCreatorsJSON =
        mapOf<ActivityEnum, (BasicActivity, JsonObject?) -> GenericActivity>(
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
                    stepsCount = extractLong(jsonData!!, "stepsCount"),
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
                    stepsCount = extractLong(jsonData!!, "stepsCount"),
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
            }

        )
}