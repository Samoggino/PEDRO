package com.lam.pedro.data.serializers

import androidx.health.connect.client.records.SleepSessionRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

object StageSerializer : KSerializer<SleepSessionRecord.Stage> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Stage") {
        element("startTime", InstantSerializer.descriptor)
        element("endTime", InstantSerializer.descriptor)
        element("stage", Int.serializer().descriptor)  // stage è un intero
    }

    override fun serialize(encoder: Encoder, value: SleepSessionRecord.Stage) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
        compositeOutput.encodeSerializableElement(descriptor, 1, InstantSerializer, value.endTime)
        compositeOutput.encodeIntElement(descriptor, 2, value.stage)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): SleepSessionRecord.Stage {
        val compositeInput = decoder.beginStructure(descriptor)
        var startTime: Instant? = null
        var endTime: Instant? = null
        var stage: Int? = null

        loop@ while (true) {
            when (val index = compositeInput.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> startTime =
                    compositeInput.decodeSerializableElement(descriptor, 0, InstantSerializer)

                1 -> endTime =
                    compositeInput.decodeSerializableElement(descriptor, 1, InstantSerializer)

                2 -> stage = compositeInput.decodeIntElement(descriptor, 2)
                else -> throw SerializationException("Unexpected index $index")
            }
        }
        compositeInput.endStructure(descriptor)

        // Restituisci il tipo originale
        return SleepSessionRecord.Stage(startTime!!, endTime!!, stage!!)
    }
}
