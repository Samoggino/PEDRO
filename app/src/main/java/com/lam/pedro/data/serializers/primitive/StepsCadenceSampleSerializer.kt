package com.lam.pedro.data.serializers.primitive

import android.util.Log
import androidx.health.connect.client.records.StepsCadenceRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

object StepsCadenceSampleSerializer : KSerializer<StepsCadenceRecord.Sample> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StepsCadenceSample") {
        element("time", InstantSerializer.descriptor)
        element<Double>("rate")
    }

    override fun serialize(encoder: Encoder, value: StepsCadenceRecord.Sample) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.time)
                encodeDoubleElement(descriptor, 1, value.rate)
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing StepsCadenceSample", e)
        }
    }

    override fun deserialize(decoder: Decoder): StepsCadenceRecord.Sample {
        return decoder.decodeStructure(descriptor) {
            var time: Instant = Instant.EPOCH
            var rate: Double = 0.0

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> time = decodeSerializableElement(descriptor, 0, InstantSerializer)
                    1 -> rate = decodeDoubleElement(descriptor, 1)
                    else -> break
                }
            }
            StepsCadenceRecord.Sample(time, rate)
        }
    }
}
