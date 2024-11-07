package com.lam.pedro.data.serializers.primitive

import androidx.health.connect.client.records.StepsRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant
import java.time.ZoneOffset

/***
 * StepsRecord(
 *     startTime: Instant,
 *     startZoneOffset: ZoneOffset?,
 *     endTime: Instant,
 *     endZoneOffset: ZoneOffset?,
 *     count: @IntRange(from = 1, to = 1000000) Long,
 *     metadata: Metadata = Metadata.EMPTY
 * )
 */


object StepsRecordSerializer : KSerializer<StepsRecord> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StepsRecord") {
        element("startTime", InstantSerializer.descriptor)
        element("startZoneOffset", ZoneOffsetSerializer.descriptor)
        element("endTime", InstantSerializer.descriptor)
        element("endZoneOffset", ZoneOffsetSerializer.descriptor)
        element("count", Long.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): StepsRecord {
        return decoder.decodeStructure(descriptor) {
            var startTime: Instant? = null
            var endTime: Instant? = null
            var startZoneOffset: ZoneOffset? = null
            var endZoneOffset: ZoneOffset? = null
            var count: Long = 0

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> startTime = decodeSerializableElement(descriptor, 0, InstantSerializer)
                    1 -> startZoneOffset =
                        decodeSerializableElement(descriptor, 1, ZoneOffsetSerializer)

                    2 -> endTime = decodeSerializableElement(descriptor, 2, InstantSerializer)
                    3 -> endZoneOffset =
                        decodeSerializableElement(descriptor, 3, ZoneOffsetSerializer)

                    4 -> count = decodeLongElement(descriptor, 4)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            StepsRecord(
                startTime = startTime!!,
                startZoneOffset = startZoneOffset,
                endTime = endTime!!,
                endZoneOffset = endZoneOffset,
                count = count
            )

        }
    }

    override fun serialize(encoder: Encoder, value: StepsRecord) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
            if (value.startZoneOffset != null) {
                encodeSerializableElement(
                    descriptor,
                    1,
                    ZoneOffsetSerializer,
                    value.startZoneOffset as ZoneOffset
                )
            }
            encodeSerializableElement(descriptor, 2, InstantSerializer, value.endTime)
            if (value.endZoneOffset != null) {
                encodeSerializableElement(
                    descriptor,
                    3,
                    ZoneOffsetSerializer,
                    value.endZoneOffset as ZoneOffset
                )
            }
            encodeLongElement(descriptor, 4, value.count)
        }
    }

}