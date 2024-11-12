package com.lam.pedro.data.activitySession

import androidx.health.connect.client.units.Volume
import java.time.Instant
import kotlin.random.Random

data class SitSession(
    override val startTime: Instant,
    override val endTime: Instant,
    override val title: String = "My Sit #${Random.nextInt(0, Int.MAX_VALUE)}",
    override val notes: String,
    val volume: Volume
) : ActivitySession(startTime, endTime, title, notes)










