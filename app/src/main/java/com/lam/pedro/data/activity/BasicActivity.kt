@file:UseSerializers(
    InstantSerializer::class
)

package com.lam.pedro.data.activity

import com.lam.pedro.data.serializers.primitive.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import kotlin.random.Random

@Serializable
data class BasicActivity(
    val startTime: Instant,
    val endTime: Instant,
    val title: String = "My Activity #${Random.nextInt(0, Int.MAX_VALUE)}",
    val notes: String
)

@Serializable
sealed class ActivityInterface(
    open val activityType: ActivityType
) {
    abstract val basicActivity: BasicActivity
}
