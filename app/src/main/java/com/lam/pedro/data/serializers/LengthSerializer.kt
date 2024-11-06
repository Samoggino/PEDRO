package com.lam.pedro.data.serializers

import androidx.health.connect.client.units.Length
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LengthSerializer : KSerializer<Length> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Length") {
        element<Double>("valueInMeters") // aspettati un numero
    }

    // Serializza come un valore numerico
    override fun serialize(encoder: Encoder, value: Length) {
        encoder.encodeDouble(value.inMeters)
    }

    // Deserializza un numero e lo converte in un oggetto Length
    override fun deserialize(decoder: Decoder): Length {
        val valueInMeters = decoder.decodeDouble()  // Decodifica come Double
        return Length.meters(valueInMeters)         // Converte in Length
    }
}
