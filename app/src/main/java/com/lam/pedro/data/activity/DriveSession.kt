@file:UseSerializers(
    LengthSerializer::class,
    ExerciseRouteSerializer::class,
    SpeedRecordSampleSerializer::class,
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.lists.SpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class DriveSession(
    override val basicActivity: BasicActivity,

    val speedSamples: List<SpeedRecord.Sample>,
    val distance: Length,
    val elevationGained: Length,
    val exerciseRoute: ExerciseRoute,
) : ActivityInterface()