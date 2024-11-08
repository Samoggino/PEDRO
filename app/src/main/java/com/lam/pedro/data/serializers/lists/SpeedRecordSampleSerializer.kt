package com.lam.pedro.data.serializers.lists

import android.util.Log
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Velocity
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import com.lam.pedro.data.serializers.primitive.VelocitySerializer
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
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SpeedRecordSample") {
        try {
            element("time", InstantSerializer.descriptor)
            element("speed", VelocitySerializer.descriptor)
        } catch (e: Exception) {
            Log.e("Creating", "Error creating SpeedSample descriptor", e)
            throw SerializationException("Error creating SpeedSample descriptor", e)
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
            Log.e("Deserializing", "Error deserializing SpeedSample", e)
            throw SerializationException("Error deserializing SpeedSample", e)
        }
    }

    override fun serialize(encoder: Encoder, value: SpeedRecord.Sample) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.time)
                encodeSerializableElement(descriptor, 1, VelocitySerializer, value.speed)
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing SpeedSample", e)
            throw SerializationException("Error serializing SpeedSample", e)
        }
    }
}