package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.units.Velocity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object VelocitySerializer : KSerializer<Velocity> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Velocity", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Velocity) {
        encoder.encodeDouble(value.inMetersPerSecond) // serializza come numero
    }

    override fun deserialize(decoder: Decoder): Velocity {
        val speed = decoder.decodeDouble() // deserializza come numero
        return Velocity.metersPerSecond(speed)
    }
}
