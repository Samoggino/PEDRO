package com.lam.pedro.data.serializers.primitive

import android.util.Log
import androidx.health.connect.client.records.ExerciseSegment
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

object ExerciseSegmentSerializer : KSerializer<ExerciseSegment> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ExerciseSegment") {
            try {
                element<Int>("segmentType")
                element<Int>("repetitions")
                element("startTime", InstantSerializer.descriptor)
                element("endTime", InstantSerializer.descriptor)
            } catch (e: Exception) {
                Log.e("Creating", "Error creating ExerciseSegmentSerializer descriptor", e)
                throw Exception("Error creating ExerciseSegmentSerializer descriptor", e)
            }
        }


    override fun serialize(encoder: Encoder, value: ExerciseSegment) {
        try {
            encoder.beginStructure(descriptor).apply {
                encodeIntElement(descriptor, 0, value.segmentType)
                encodeIntElement(descriptor, 1, value.repetitions)
                encodeSerializableElement(descriptor, 2, InstantSerializer, value.startTime)
                encodeSerializableElement(descriptor, 3, InstantSerializer, value.endTime)
                endStructure(descriptor)
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ExerciseSegment", e)
            throw Exception("Error serializing ExerciseSegment", e)
        }
    }

    override fun deserialize(decoder: Decoder): ExerciseSegment {
        try {
            val compositeDecoder = decoder.beginStructure(descriptor)
            var startTime = Instant.now()
            var endTime = Instant.now()
            var segmentType = 0
            var repetitions = 0
            loop@ while (true) {
                when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                    0 -> segmentType = compositeDecoder.decodeIntElement(descriptor, 0)
                    1 -> repetitions = compositeDecoder.decodeIntElement(descriptor, 1)
                    2 -> startTime =
                        compositeDecoder.decodeSerializableElement(descriptor, 2, InstantSerializer)

                    3 -> endTime =
                        compositeDecoder.decodeSerializableElement(descriptor, 3, InstantSerializer)

                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> error("Unexpected index: $index")
                }
            }
            compositeDecoder.endStructure(descriptor)
            return ExerciseSegment(startTime, endTime, segmentType, repetitions)
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing ExerciseSegment", e)
            throw Exception("Error deserializing ExerciseSegment", e)
        }
    }
}