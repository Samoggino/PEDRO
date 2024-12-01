@file:UseSerializers(VolumeSerializer::class)

package com.lam.pedro.data.activity

import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.serializers.primitive.VolumeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class SitSession(
    override val basicActivity: BasicActivity,
    val volume: Volume
) : GenericActivity(activityType = ActivityType.SIT)
