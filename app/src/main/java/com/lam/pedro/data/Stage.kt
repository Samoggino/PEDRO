@file:UseSerializers(InstantSerializer::class)

package com.lam.pedro.data

import com.lam.pedro.data.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

// Classe serializzabile per Stage
@Serializable
data class StageSerializable(
    val startTime: Instant,
    val endTime: Instant,
    val stage: Int
)
