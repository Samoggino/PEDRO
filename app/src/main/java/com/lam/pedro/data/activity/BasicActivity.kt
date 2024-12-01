@file:UseSerializers(
    InstantSerializer::class
)

package com.lam.pedro.data.activity

import com.lam.pedro.data.serializers.primitive.InstantSerializer
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant
import java.time.ZoneId
import kotlin.random.Random

@Serializable
data class BasicActivity(
    val startTime: Instant,
    val endTime: Instant,
    val title: String = "My Activity #${Random.nextInt(0, Int.MAX_VALUE)}",
    val notes: String
)


fun Instant.toMonthNumber(zoneId: ZoneId = ZoneId.systemDefault()): Int {
    return this.atZone(zoneId).monthValue
}

fun Int.toMonthString(): String {
    return Month.entries[this - 1].name
}

@Serializable
sealed class GenericActivity(
    open val activityType: ActivityType
) {
    abstract val basicActivity: BasicActivity
}
