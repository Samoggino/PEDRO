package com.lam.pedro.data.serializers

import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant
import java.time.ZoneOffset

object TotalCaloriesBurnedRecordSerializer : KSerializer<TotalCaloriesBurnedRecord> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("TotalCaloriesBurnedRecord") {
            element<String>("startTime")
            element<String>("startZoneOffset")
            element<String>("endTime")
            element<String>("endZoneOffset")
            element<Energy>("energy")
        }

    override fun serialize(encoder: Encoder, value: TotalCaloriesBurnedRecord) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.startTime.toString())
            value.startZoneOffset?.let { encodeStringElement(descriptor, 1, it.id) }
            encodeStringElement(descriptor, 2, value.endTime.toString())
            value.endZoneOffset?.let { encodeStringElement(descriptor, 3, it.id) }
            encodeSerializableElement(descriptor, 4, EnergySerializer, value.energy)
        }
    }

    override fun deserialize(decoder: Decoder): TotalCaloriesBurnedRecord {
        return decoder.decodeStructure(descriptor) {
            var startTime: Instant? = null
            var startZoneOffset: ZoneOffset? = null
            var endTime: Instant? = null
            var endZoneOffset: ZoneOffset? = null
            var energy: Energy? = null

            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> startTime = Instant.parse(decodeStringElement(descriptor, index))
                    1 -> startZoneOffset =
                        decodeStringElement(descriptor, index).let { ZoneOffset.of(it) }

                    2 -> endTime = Instant.parse(decodeStringElement(descriptor, index))
                    3 -> endZoneOffset =
                        decodeStringElement(descriptor, index).let { ZoneOffset.of(it) }

                    4 -> energy = decodeSerializableElement(descriptor, index, EnergySerializer)


                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> throw IllegalStateException("Unexpected index: $index")
                }
            }

            TotalCaloriesBurnedRecord(
                startTime ?: throw IllegalStateException("startTime is required"),
                startZoneOffset,
                endTime ?: throw IllegalStateException("endTime is required"),
                endZoneOffset,
                energy ?: throw IllegalStateException("energy is required")
            )
        }
    }
}
