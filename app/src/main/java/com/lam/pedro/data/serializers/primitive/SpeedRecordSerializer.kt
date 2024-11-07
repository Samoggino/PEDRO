package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.records.SpeedRecord
import com.lam.pedro.data.serializers.lists.ListSpeedRecordSampleSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant
import java.time.ZoneOffset

object SpeedRecordSerializer : KSerializer<SpeedRecord> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SpeedRecord") {
        element("startTime", InstantSerializer.descriptor)
        element("startZoneOffset", ZoneOffsetSerializer.descriptor)
        element("endTime", InstantSerializer.descriptor)
        element("endZoneOffset", ZoneOffsetSerializer.descriptor)
        element("samples", ListSpeedRecordSampleSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): SpeedRecord {
        return decoder.decodeStructure(descriptor) {
            var startTime: Instant? = null
            var startZoneOffset: ZoneOffset? = null
            var endTime: Instant? = null
            var endZoneOffset: ZoneOffset? = null
            var samples: List<SpeedRecord.Sample> = emptyList()

            // Leggi i valori degli elementi dalla struttura
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> startTime = decodeSerializableElement(descriptor, 0, InstantSerializer)
                    1 -> startZoneOffset =
                        decodeSerializableElement(descriptor, 1, ZoneOffsetSerializer)

                    2 -> endTime = decodeSerializableElement(descriptor, 2, InstantSerializer)
                    3 -> endZoneOffset =
                        decodeSerializableElement(descriptor, 3, ZoneOffsetSerializer)

                    4 -> samples =
                        decodeSerializableElement(descriptor, 4, ListSpeedRecordSampleSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            // Gestisci valori nulli (potrebbero essere opzionali)
            SpeedRecord(
                startTime ?: throw SerializationException("Missing startTime"),
                startZoneOffset,
                endTime ?: throw SerializationException("Missing endTime"),
                endZoneOffset,
                samples
            )
        }
    }

    override fun serialize(encoder: Encoder, value: SpeedRecord) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
            value.startZoneOffset?.let {
                encodeSerializableElement(descriptor, 1, ZoneOffsetSerializer, it)
            }
            encodeSerializableElement(descriptor, 2, InstantSerializer, value.endTime)
            value.endZoneOffset?.let {
                encodeSerializableElement(descriptor, 3, ZoneOffsetSerializer, it)
            }
            encodeSerializableElement(descriptor, 4, ListSpeedRecordSampleSerializer, value.samples)
        }
    }
}
