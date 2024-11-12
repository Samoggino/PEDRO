package com.lam.pedro.data.activitySession

import java.time.Instant
import kotlin.random.Random

data class SleepSession(
    override val startTime: Instant,
    override val endTime: Instant,
    override val title: String = "My Sleep #${Random.nextInt(0, Int.MAX_VALUE)}",
    override val notes: String
) : ActivitySession(startTime, endTime, title, notes)
