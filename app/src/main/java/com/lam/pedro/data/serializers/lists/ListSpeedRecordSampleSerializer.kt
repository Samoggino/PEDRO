package com.lam.pedro.data.serializers.lists

import android.util.Log
import androidx.health.connect.client.records.SpeedRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ListSpeedRecordSampleSerializer : KSerializer<List<SpeedRecord.Sample>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ListSpeedRecordSample") {
            try {
                element("samples", ListSerializer(SpeedRecordSampleSerializer).descriptor)
            } catch (e: Exception) {
                Log.e("Creating", "Error creating ListSample descriptor", e)
                throw SerializationException("Error creating ListSample descriptor", e)
            }
        }

    override fun deserialize(decoder: Decoder): List<SpeedRecord.Sample> {
        try {
            return ListSerializer(SpeedRecordSampleSerializer).deserialize(decoder)
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing ListSample", e)
            throw SerializationException("Error deserializing ListSample", e)
        }
    }

    override fun serialize(encoder: Encoder, value: List<SpeedRecord.Sample>) {
        try {
            ListSerializer(SpeedRecordSampleSerializer).serialize(encoder, value)
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ListSample", e)
            throw SerializationException("Error serializing ListSample", e)
        }
    }
}
