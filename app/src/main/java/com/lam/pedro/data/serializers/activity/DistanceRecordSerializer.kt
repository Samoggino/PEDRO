package com.lam.pedro.data.serializers.activity

import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import com.lam.pedro.data.serializers.primitive.ZoneOffsetSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant
import java.time.ZoneOffset

/**
 * DistanceRecord(
 *     startTime: Instant,
 *     startZoneOffset: ZoneOffset?,
 *     endTime: Instant,
 *     endZoneOffset: ZoneOffset?,
 *     distance: Length,
 *     metadata: Metadata = Metadata.EMPTY
 * )
 */


object DistanceRecordSerializer : KSerializer<DistanceRecord> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DistanceRecord") {
        element("startTime", InstantSerializer.descriptor)
        element("startZoneOffset", ZoneOffsetSerializer.descriptor, isOptional = true)
        element("endTime", InstantSerializer.descriptor)
        element("endZoneOffset", ZoneOffsetSerializer.descriptor, isOptional = true)
        element("distance", LengthSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): DistanceRecord {
        return decoder.decodeStructure(descriptor) {
            var startTime: Instant? = null
            var startZoneOffset: ZoneOffset? = null
            var endTime: Instant? = null
            var endZoneOffset: ZoneOffset? = null
            var distance: Length? = null

            loop@ while (true) {
                when (decodeElementIndex(descriptor)) {
                    0 -> startTime = InstantSerializer.deserialize(decoder)
                    1 -> startZoneOffset = ZoneOffsetSerializer.deserialize(decoder)
                    2 -> endTime = InstantSerializer.deserialize(decoder)
                    3 -> endZoneOffset = ZoneOffsetSerializer.deserialize(decoder)
                    4 -> distance = LengthSerializer.deserialize(decoder)

                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> error("Unexpected index")
                }
            }

            DistanceRecord(
                startTime = startTime!!,
                startZoneOffset = startZoneOffset,
                endTime = endTime!!,
                endZoneOffset = endZoneOffset,
                distance = distance!!
            )
        }
    }

    override fun serialize(encoder: Encoder, value: DistanceRecord) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
            value.startZoneOffset?.let {
                encodeSerializableElement(
                    descriptor,
                    1,
                    ZoneOffsetSerializer,
                    it
                )
            }
            encodeSerializableElement(descriptor, 2, InstantSerializer, value.endTime)
            value.endZoneOffset?.let {
                encodeSerializableElement(
                    descriptor,
                    3,
                    ZoneOffsetSerializer,
                    it
                )
            }
            encodeSerializableElement(descriptor, 4, LengthSerializer, value.distance)
        }
    }
}