package com.lam.pedro.data.serializers.lists

import android.util.Log
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

object CyclingCadenceSampleSerializer : KSerializer<CyclingPedalingCadenceRecord.Sample> {
    override val descriptor = buildClassSerialDescriptor("CyclingCadenceSample") {
        try {
            element("time", InstantSerializer.descriptor)
            element(
                "revolutionsPerMinute",
                PrimitiveSerialDescriptor("revolutionsPerMinute", PrimitiveKind.DOUBLE)
            )
        } catch (e: Exception) {
            Log.e("Creating", "Error creating CyclingSample descriptor", e)
            throw SerializationException("Error creating CyclingSample descriptor", e)
        }
    }

    override fun deserialize(decoder: Decoder): CyclingPedalingCadenceRecord.Sample {
        try {
            return decoder.decodeStructure(descriptor) {
                var time = Instant.MIN
                var revolutionsPerMinute = 0.0

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> time = decodeSerializableElement(descriptor, 0, InstantSerializer)
                        1 -> revolutionsPerMinute = decodeDoubleElement(descriptor, 1)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }

                CyclingPedalingCadenceRecord.Sample(time, revolutionsPerMinute)
            }
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing CyclingSample", e)
            throw SerializationException("Error deserializing Sample", e)
        }
    }

    override fun serialize(encoder: Encoder, value: CyclingPedalingCadenceRecord.Sample) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.time)
                encodeDoubleElement(descriptor, 1, value.revolutionsPerMinute)
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing CyclingSample", e)
            throw SerializationException("Error serializing Sample", e)
        }
    }
}