package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.units.Volume
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object VolumeSerializer : KSerializer<Volume> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Volume", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Volume) {
        encoder.encodeDouble(value.inLiters)
    }

    override fun deserialize(decoder: Decoder): Volume {
        val volumeDouble = decoder.decodeDouble() // deserializza come numero
        return Volume.liters(volumeDouble)
    }
}