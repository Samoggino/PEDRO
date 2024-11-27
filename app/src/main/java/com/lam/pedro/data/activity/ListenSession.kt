package com.lam.pedro.data.activity

import kotlinx.serialization.Serializable

@Serializable
data class ListenSession(
    override val basicActivity: BasicActivity,
) : ActivityInterface(activityType = ActivityType.LISTEN)