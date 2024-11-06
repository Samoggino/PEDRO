package com.lam.pedro.data.serializers

import androidx.health.connect.client.units.Energy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object EnergySerializer : KSerializer<Energy> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Energy") {
        element<Double>("inKilocalories") // Definiamo solo il campo kilocalorie
    }

    override fun serialize(encoder: Encoder, value: Energy) {
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(
                descriptor,
                0,
                value.inKilocalories
            )
        }
    }

    override fun deserialize(decoder: Decoder): Energy {
        return decoder.decodeStructure(descriptor) {
            var inKilocalories = 0.0 // Inizializziamo inKilocalories

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> inKilocalories = decodeDoubleElement(descriptor, 0)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index $index")
                }
            }

            Energy.kilocalories(inKilocalories) // Restituiamo l'oggetto Energy
        }
    }
}