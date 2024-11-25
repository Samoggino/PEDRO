package com.lam.pedro.data.serializers.lists

import androidx.health.connect.client.records.ExerciseSegment
import com.lam.pedro.data.serializers.primitive.ExerciseSegmentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ExerciseSegmentListSerializer : KSerializer<List<ExerciseSegment>> {
    override val descriptor: SerialDescriptor = ListSerializer(ExerciseSegmentSerializer).descriptor

    override fun serialize(encoder: Encoder, value: List<ExerciseSegment>) {
        encoder.encodeSerializableValue(ListSerializer(ExerciseSegmentSerializer), value)
    }

    override fun deserialize(decoder: Decoder): List<ExerciseSegment> {
        return decoder.decodeSerializableValue(ListSerializer(ExerciseSegmentSerializer))
    }
}