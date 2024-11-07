package com.lam.pedro.data.serializers.activity

import androidx.health.connect.client.records.ExerciseCompletionGoal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

object DurationGoalSerializer : KSerializer<ExerciseCompletionGoal.DurationGoal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "DurationGoal",
            kotlinx.serialization.descriptors.PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: ExerciseCompletionGoal.DurationGoal) {
        // Serializza solo la durata come stringa
        encoder.encodeString(value.duration.toString())
    }

    override fun deserialize(decoder: Decoder): ExerciseCompletionGoal.DurationGoal {
        // Deserializza la stringa in un Duration e crea un oggetto DurationGoal
        val duration = Duration.parse(decoder.decodeString())
        return ExerciseCompletionGoal.DurationGoal(duration)
    }
}
