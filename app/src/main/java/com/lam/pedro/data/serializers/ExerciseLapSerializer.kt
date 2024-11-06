package com.lam.pedro.data.serializers

import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.units.Length
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

object ExerciseLapSerializer : KSerializer<ExerciseLap> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExerciseLap") {
        element<Instant>("startTime") // startTime come Instant
        element<Instant>("endTime")   // endTime come Instant
        element("length", LengthSerializer.descriptor) // length come oggetto Length
    }

    override fun serialize(encoder: Encoder, value: ExerciseLap) {
        encoder.encodeStructure(descriptor) {
            // Serializziamo startTime e endTime come oggetti Instant
            encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
            encodeSerializableElement(descriptor, 1, InstantSerializer, value.endTime)

            // Se length è presente, serializziamo usando il serializzatore di Length
            value.length?.let {
                encodeSerializableElement(descriptor, 2, LengthSerializer, it)
            }
        }
    }

    override fun deserialize(decoder: Decoder): ExerciseLap {
        return decoder.decodeStructure(descriptor) {
            var startTime: Instant? = null
            var endTime: Instant? = null
            var length: Length? = null

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> startTime = decodeSerializableElement(
                        descriptor,
                        index,
                        InstantSerializer
                    ) // Deserializziamo startTime come Instant
                    1 -> endTime = decodeSerializableElement(
                        descriptor,
                        index,
                        InstantSerializer
                    )   // Deserializziamo endTime come Instant
                    2 -> length = decodeSerializableElement(
                        descriptor,
                        index,
                        LengthSerializer
                    )     // Deserializziamo length come oggetto Length
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw IllegalStateException("Unexpected index: $index")
                }
            }

            // Restituiamo l'oggetto ExerciseLap con i valori deserializzati
            ExerciseLap(
                startTime ?: throw IllegalStateException("startTime is required"),
                endTime ?: throw IllegalStateException("endTime is required"),
                length
            )
        }
    }
}