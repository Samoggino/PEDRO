package com.lam.pedro.data.activitySession

import java.time.Instant
import kotlin.random.Random
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

abstract class ActivitySession(
    open val startTime: Instant,
    open val endTime: Instant,
    open val title: String = "My Activity #${Random.nextInt(0, Int.MAX_VALUE)}",
    open val notes: String
)

