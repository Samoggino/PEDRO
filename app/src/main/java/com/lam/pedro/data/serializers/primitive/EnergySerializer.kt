package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.units.Energy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object EnergySerializer : KSerializer<Energy> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Energy") {
        element<Double>("inKilocalories") // Definiamo solo il campo kilocalorie
    }

    override fun serialize(encoder: Encoder, value: Energy) {
        encoder.encodeDouble(value.inKilocalories)  // Serializza come Double
    }

    override fun deserialize(decoder: Decoder): Energy {
        val kcalDouble = decoder.decodeDouble()  // Decodifica come Double
        return Energy.kilocalories(kcalDouble)   // Converte in Energy
    }
}