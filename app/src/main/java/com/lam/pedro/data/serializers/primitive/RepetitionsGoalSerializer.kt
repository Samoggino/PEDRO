package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.records.ExerciseCompletionGoal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object RepetitionsGoalSerializer : KSerializer<ExerciseCompletionGoal.RepetitionsGoal> {
    override val descriptor = buildClassSerialDescriptor("ExerciseCompletionGoal.RepetitionsGoal") {
        PrimitiveSerialDescriptor("repetition", PrimitiveKind.INT)
    }

    override fun serialize(encoder: Encoder, value: ExerciseCompletionGoal.RepetitionsGoal) {
        encoder.encodeInt(value.repetitions)
    }

    override fun deserialize(decoder: Decoder): ExerciseCompletionGoal.RepetitionsGoal {
        val repetitions = decoder.decodeInt()
        return ExerciseCompletionGoal.RepetitionsGoal(repetitions)
    }

}

