package com.lam.pedro.data.serializers.activity

import android.util.Log
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import com.lam.pedro.data.serializers.primitive.ZoneOffsetSerializer
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


object DistanceRecordSerializer : KSerializer<DistanceRecord> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DistanceRecord") {
            element("startTime", InstantSerializer.descriptor)
            element("startZoneOffset", ZoneOffsetSerializer.descriptor, isOptional = true)
            element("endTime", InstantSerializer.descriptor)
            element("endZoneOffset", ZoneOffsetSerializer.descriptor, isOptional = true)
            element("distance", LengthSerializer.descriptor)
        }

    override fun serialize(encoder: Encoder, value: DistanceRecord) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.startTime.toString())
            value.startZoneOffset?.let { encodeStringElement(descriptor, 1, it.id) }
            encodeStringElement(descriptor, 2, value.endTime.toString())
            value.endZoneOffset?.let { encodeStringElement(descriptor, 3, it.id) }
            encodeSerializableElement(descriptor, 4, LengthSerializer, value.distance)
        }
    }

    override fun deserialize(decoder: Decoder): DistanceRecord {
        return try {
            decoder.decodeStructure(descriptor) {
                var startTime: Instant? = null
                var startZoneOffset: ZoneOffset? = null
                var endTime: Instant? = null
                var endZoneOffset: ZoneOffset? = null
                var distance: Length? = null

                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> startTime = Instant.parse(decodeStringElement(descriptor, index))
                        1 -> startZoneOffset =
                            decodeStringElement(descriptor, index).let { ZoneOffset.of(it) }

                        2 -> endTime = Instant.parse(decodeStringElement(descriptor, index))
                        3 -> endZoneOffset =
                            decodeStringElement(descriptor, index).let { ZoneOffset.of(it) }

                        4 -> distance =
                            decodeSerializableElement(descriptor, index, LengthSerializer)

                        CompositeDecoder.DECODE_DONE -> break@loop

                        else -> throw SerializationException("Unknown index: $index")

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
        } catch (e: Exception) {
            Log.e("Serializing", "Error: ${e.message}")
            throw SerializationException("Error: ${e.message}")
        }
    }
}