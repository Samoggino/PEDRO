package com.lam.pedro.data.serializers.primitive

import android.util.Log
import androidx.health.connect.client.units.Length
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LengthSerializer : KSerializer<Length> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Length") {
        try {
            element<Double>("valueInMeters")
        } catch (e: Exception) {
            Log.e("Creating", "Error creating Length descriptor", e)
            throw e
        }
    }

    // Serializza come un valore numerico
    override fun serialize(encoder: Encoder, value: Length) {
        try {
            encoder.encodeDouble(value.inMeters)
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing Length", e)
            throw e
        }
    }

    // Deserializza un numero e lo converte in un oggetto Length
    override fun deserialize(decoder: Decoder): Length {
        try {
            val valueInMeters = decoder.decodeDouble()  // Decodifica come Double
            return Length.meters(valueInMeters)         // Converte in Length
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing Length", e)
            throw e
        }
    }
}
