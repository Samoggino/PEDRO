package com.lam.pedro.data.serializers.lists

import androidx.health.connect.client.records.ExerciseLap
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ListExerciseLapSerializer : KSerializer<List<ExerciseLap>> {
    override val descriptor: SerialDescriptor = ListSerializer(ExerciseLapSerializer).descriptor

    override fun serialize(encoder: Encoder, value: List<ExerciseLap>) {
        encoder.encodeSerializableValue(ListSerializer(ExerciseLapSerializer), value)
    }

    override fun deserialize(decoder: Decoder): List<ExerciseLap> {
        return decoder.decodeSerializableValue(ListSerializer(ExerciseLapSerializer))
    }
}