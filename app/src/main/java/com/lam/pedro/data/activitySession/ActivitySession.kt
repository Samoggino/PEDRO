package com.lam.pedro.data.activitySession

import java.time.Instant
import kotlin.random.Random

abstract class ActivitySession(
    open val startTime: Instant,
    open val endTime: Instant,
    open val title: String = "My Activity #${Random.nextInt(0, Int.MAX_VALUE)}",
    open val notes: String
)

