@file:UseSerializers(
    EnergySerializer::class,
    LengthSerializer::class,
    ExerciseRouteSerializer::class,
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.lists.ListCyclingPedalingCadenceRecordSample
import com.lam.pedro.data.serializers.lists.ListSpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class CyclingSession(
    override val basicActivity: BasicActivity,
    val totalEnergy: Energy,
    val activeEnergy: Energy,

    @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
    @Serializable(with = ListCyclingPedalingCadenceRecordSample::class) val cyclingPedalingCadenceSamples: List<@Contextual CyclingPedalingCadenceRecord.Sample>,
    val distance: Length,
    val elevationGained: Length,
    val exerciseRoute: ExerciseRoute,
) : ActivityInterface(activityType = ActivityType.CYCLING)
