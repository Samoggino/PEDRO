package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Velocity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

/***
 * Sample(time: Instant, speed: Velocity)
 */

object SpeedRecordSampleSerializer : KSerializer<SpeedRecord.Sample> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Sample") {
        try {
            element("time", InstantSerializer.descriptor)
            element("speed", VelocitySerializer.descriptor)
        } catch (e: Exception) {
            throw SerializationException("Error creating Sample descriptor", e)
        }
    }

    override fun deserialize(decoder: Decoder): SpeedRecord.Sample {
        try {
            val dec = decoder.beginStructure(descriptor)
            var time = Instant.MIN
            var speed = Velocity.metersPerSecond(0.0)

            loop@ while (true) {
                when (val index = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> time = dec.decodeSerializableElement(descriptor, index, InstantSerializer)
                    1 -> speed =
                        dec.decodeSerializableElement(descriptor, index, VelocitySerializer)

                    else -> throw SerializationException("Unknown index $index")
                }
            }
            dec.endStructure(descriptor)
            return SpeedRecord.Sample(time, speed)
        } catch (e: Exception) {
            throw SerializationException("Error deserializing Sample", e)
        }
    }

    override fun serialize(encoder: Encoder, value: SpeedRecord.Sample) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.time)
                encodeSerializableElement(descriptor, 1, VelocitySerializer, value.speed)
            }
        } catch (e: Exception) {
            throw SerializationException("Error serializing Sample", e)
        }
    }
}