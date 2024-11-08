package com.lam.pedro.data.serializers.primitive

import android.util.Log
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import com.lam.pedro.data.serializers.lists.ListCyclingPedalingCadenceRecordSample
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

object CyclingPedalingCadenceRecordSerializer : KSerializer<CyclingPedalingCadenceRecord> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("CyclingPedalingCadenceRecord") {
            try {
                element("startTime", InstantSerializer.descriptor)
                element("startZoneOffset", ZoneOffsetSerializer.descriptor)
                element("endTime", InstantSerializer.descriptor)
                element("endZoneOffset", ZoneOffsetSerializer.descriptor)
                element("samples", ListCyclingPedalingCadenceRecordSample.descriptor)
            } catch (e: Exception) {
                Log.e("Creating", "Error creating CyclingCadence descriptor", e)
                throw SerializationException("Error creating CyclingCadence descriptor", e)
            }
        }

    override fun deserialize(decoder: Decoder): CyclingPedalingCadenceRecord {
        try {
            return decoder.decodeStructure(descriptor) {
                var startTime: Instant? = null
                var startZoneOffset: ZoneOffset? = null
                var endTime: Instant? = null
                var endZoneOffset: ZoneOffset? = null
                var samples: List<CyclingPedalingCadenceRecord.Sample> = emptyList()

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> startTime = decodeSerializableElement(descriptor, 0, InstantSerializer)
                        1 -> startZoneOffset =
                            decodeSerializableElement(descriptor, 1, ZoneOffsetSerializer)

                        2 -> endTime = decodeSerializableElement(descriptor, 2, InstantSerializer)
                        3 -> endZoneOffset =
                            decodeSerializableElement(descriptor, 3, ZoneOffsetSerializer)

                        4 -> samples =
                            decodeSerializableElement(
                                descriptor,
                                4,
                                ListCyclingPedalingCadenceRecordSample
                            )

                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }



                CyclingPedalingCadenceRecord(
                    startTime!!,
                    startZoneOffset,
                    endTime!!,
                    endZoneOffset,
                    samples
                )
            }
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing CyclingCadence", e)
            throw SerializationException("Error deserializing CyclingCadence", e)
        }
    }

    override fun serialize(encoder: Encoder, value: CyclingPedalingCadenceRecord) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
                value.startZoneOffset?.let {
                    encodeSerializableElement(descriptor, 1, ZoneOffsetSerializer, it)
                }
                encodeSerializableElement(descriptor, 2, InstantSerializer, value.endTime)
                value.endZoneOffset?.let {
                    encodeSerializableElement(descriptor, 3, ZoneOffsetSerializer, it)
                }
                encodeSerializableElement(
                    descriptor,
                    4,
                    ListCyclingPedalingCadenceRecordSample,
                    value.samples
                )
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing CyclingCadence", e)
            throw SerializationException("Error serializing CyclingCadence", e)
        }
    }

}