package com.lam.pedro.data.serializers

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
        val compositeEncoder = encoder.beginStructure(descriptor)

        // Serializziamo ogni campo, verificando se è presente o nullo
        compositeEncoder.encodeSerializableElement(
            descriptor,
            0,
            InstantSerializer,
            value.startTime
        )

        compositeEncoder.encodeSerializableElement(
            descriptor, 1, ZoneOffsetSerializer,
            value.startZoneOffset!!
        )

        compositeEncoder.encodeSerializableElement(descriptor, 2, InstantSerializer, value.endTime)

        compositeEncoder.encodeSerializableElement(
            descriptor,
            3,
            ZoneOffsetSerializer,
            value.endZoneOffset!!
        )

        compositeEncoder.encodeSerializableElement(descriptor, 4, EnergySerializer, value.energy)

        compositeEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): ActiveCaloriesBurnedRecord {
        val compositeDecoder = decoder.beginStructure(descriptor)

        var startTime: Instant? = null
        var startZoneOffset: ZoneOffset? = null
        var endTime: Instant? = null
        var endZoneOffset: ZoneOffset? = null
        var energy: Energy? = null

        loop@ while (true) {
            when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> startTime =
                    compositeDecoder.decodeSerializableElement(descriptor, index, InstantSerializer)

                1 -> startZoneOffset = compositeDecoder.decodeSerializableElement(
                    descriptor,
                    index,
                    ZoneOffsetSerializer
                )

                2 -> endTime =
                    compositeDecoder.decodeSerializableElement(descriptor, index, InstantSerializer)

                3 -> endZoneOffset = compositeDecoder.decodeSerializableElement(
                    descriptor,
                    index,
                    ZoneOffsetSerializer
                )

                4 -> energy =
                    compositeDecoder.decodeSerializableElement(descriptor, index, EnergySerializer)

                else -> throw SerializationException("Unknown index: $index")
            }
        }

        compositeDecoder.endStructure(descriptor)

        return ActiveCaloriesBurnedRecord(
            startTime = startTime!!,
            startZoneOffset = startZoneOffset,
            endTime = endTime!!,
            endZoneOffset = endZoneOffset,
            energy = energy!!
        )

    }
}
