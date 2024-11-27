package com.lam.pedro.data.activity

import kotlinx.serialization.Serializable

@Serializable
enum class ActivityType {
    CYCLING,
    RUN,
    YOGA,
    TRAIN,
    DRIVE,
    SIT,
    SLEEP,
    WALK,
    LIFT,
    LISTEN
}