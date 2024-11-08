package com.lam.pedro.data.serializers.lists

import android.util.Log
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ListCyclingPedalingCadenceRecordSample :
    KSerializer<List<CyclingPedalingCadenceRecord.Sample>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ListCyclingPedalingCadenceRecordSample") {
            try {
                element("samples", ListSerializer(CyclingCadenceSampleSerializer).descriptor)
            } catch (e: Exception) {
                Log.e("Creating", "Error creating ListCyclingSample descriptor", e)
                throw SerializationException("Error creating ListCyclingSample descriptor", e)
            }
        }

    override fun deserialize(decoder: Decoder): List<CyclingPedalingCadenceRecord.Sample> {
        try {
            return ListSerializer(CyclingCadenceSampleSerializer).deserialize(decoder)
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing ListCyclingSample", e)
            throw SerializationException("Error deserializing ListCyclingSample", e)
        }
    }

    override fun serialize(encoder: Encoder, value: List<CyclingPedalingCadenceRecord.Sample>) {
        try {
            ListSerializer(CyclingCadenceSampleSerializer).serialize(encoder, value)
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ListCyclingSample", e)
            throw SerializationException("Error serializing ListCyclingSample", e)
        }
    }


}