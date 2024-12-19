package com.lam.pedro.data.serializers.activity

import android.util.Log
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

object ExerciseLapSerializer : KSerializer<ExerciseLap> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExerciseLap") {
        try {
            element("startTime", InstantSerializer.descriptor)
            element("endTime", InstantSerializer.descriptor)
            element("length", LengthSerializer.descriptor, isOptional = true)
        } catch (e: Exception) {
            Log.e("Creating", "Error creating ExerciseLap descriptor", e)
        }
    }

    override fun serialize(encoder: Encoder, value: ExerciseLap) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
                encodeSerializableElement(descriptor, 1, InstantSerializer, value.endTime)
                if (value.length != null) {
                    encodeSerializableElement(
                        descriptor,
                        2,
                        LengthSerializer,
                        value.length as Length
                    )
                }
            }


        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ExerciseLap", e)
        }

    }

    override fun deserialize(decoder: Decoder): ExerciseLap {
        try {
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
                    startTime = startTime ?: throw IllegalStateException("startTime is required"),
                    endTime = endTime ?: throw IllegalStateException("endTime is required"),
                    length = length
                )
            }
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing ExerciseLap", e)
            throw e
        }
    }
}