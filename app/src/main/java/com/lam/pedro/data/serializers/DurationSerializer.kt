package com.lam.pedro.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Duration")

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeLong(value.seconds)
    }

    override fun deserialize(decoder: Decoder): Duration {
        val seconds = decoder.decodeLong()
        return Duration.ofSeconds(seconds)
    }
}
