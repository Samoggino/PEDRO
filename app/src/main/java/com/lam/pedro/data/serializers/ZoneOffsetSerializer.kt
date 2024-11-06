package com.lam.pedro.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZoneOffset

object ZoneOffsetSerializer : KSerializer<ZoneOffset> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.ZoneOffset", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZoneOffset) {
        encoder.encodeString(value.id) // Serializziamo l'ID del ZoneOffset come stringa
    }

    override fun deserialize(decoder: Decoder): ZoneOffset {
        return ZoneOffset.of(decoder.decodeString()) // Deserializziamo dalla stringa
    }
}
