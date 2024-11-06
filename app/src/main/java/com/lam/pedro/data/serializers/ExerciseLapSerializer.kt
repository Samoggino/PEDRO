package com.lam.pedro.data.serializers

import android.util.Log
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.units.Length
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import java.time.Instant

object ExerciseLapSerializer : KSerializer<ExerciseLap> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExerciseLap") {
        element("startTime", InstantSerializer.descriptor)
        element("endTime", InstantSerializer.descriptor)
        element("length", LengthSerializer.descriptor, isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: ExerciseLap) {
        try {
            // Iniziamo la struttura
            val compositeEncoder = encoder.beginStructure(descriptor)

            // Serializziamo ogni campo, verificando se è presente o nullo
            compositeEncoder.encodeSerializableElement(
                descriptor,
                0,
                InstantSerializer,
                value.startTime
            )

            compositeEncoder.encodeSerializableElement(
                descriptor,
                1,
                InstantSerializer,
                value.endTime
            )

            value.length?.let {
                compositeEncoder.encodeSerializableElement(
                    descriptor,
                    2,
                    LengthSerializer,
                    it
                )
            }

            compositeEncoder.endStructure(descriptor)


        } catch (e: Exception) {
            Log.e("ExerciseLapSerializer", "Error serializing ExerciseLap", e)
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
                    )

                    1 -> endTime = decodeSerializableElement(
                        descriptor,
                        index,
                        InstantSerializer
                    )

                    2 -> length = decodeSerializableElement(
                        descriptor,
                        index,
                        LengthSerializer
                    )

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