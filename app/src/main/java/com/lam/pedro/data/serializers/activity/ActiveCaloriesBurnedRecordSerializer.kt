package com.lam.pedro.data.serializers.activity

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
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

object ActiveCaloriesBurnedRecordSerializer : KSerializer<ActiveCaloriesBurnedRecord> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ActiveCaloriesBurnedRecord") {
            element("startTime", InstantSerializer.descriptor)
            element("startZoneOffset", ZoneOffsetSerializer.descriptor, isOptional = true)
            element("endTime", InstantSerializer.descriptor)
            element("endZoneOffset", ZoneOffsetSerializer.descriptor, isOptional = true)
            element("energy", EnergySerializer.descriptor)
        }

    override fun serialize(encoder: Encoder, value: ActiveCaloriesBurnedRecord) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
            value.startZoneOffset?.let {
                encodeSerializableElement(descriptor, 1, ZoneOffsetSerializer, it)
            }
            encodeSerializableElement(descriptor, 2, InstantSerializer, value.endTime)
            value.endZoneOffset?.let {
                encodeSerializableElement(descriptor, 3, ZoneOffsetSerializer, it)
            }
            encodeSerializableElement(descriptor, 4, EnergySerializer, value.energy)
        }
    }

    override fun deserialize(decoder: Decoder): ActiveCaloriesBurnedRecord {
        return decoder.decodeStructure(descriptor) {
            var startTime: Instant? = null
            var startZoneOffset: ZoneOffset? = null
            var endTime: Instant? = null
            var endZoneOffset: ZoneOffset? = null
            var energy: Energy? = null

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> startTime = decodeSerializableElement(descriptor, index, InstantSerializer)
                    1 -> startZoneOffset =
                        decodeSerializableElement(descriptor, index, ZoneOffsetSerializer)

                    2 -> endTime = decodeSerializableElement(descriptor, index, InstantSerializer)
                    3 -> endZoneOffset =
                        decodeSerializableElement(descriptor, index, ZoneOffsetSerializer)

                    4 -> energy = decodeSerializableElement(descriptor, index, EnergySerializer)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw SerializationException("Unknown index $index")
                }
            }

            ActiveCaloriesBurnedRecord(
                startTime = startTime!!,
                startZoneOffset = startZoneOffset,
                endTime = endTime!!,
                endZoneOffset = endZoneOffset,
                energy = energy!!
            )
        }

    }
}
