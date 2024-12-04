@file:UseSerializers(
    LengthSerializer::class,
    ExerciseRouteSerializer::class,

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

