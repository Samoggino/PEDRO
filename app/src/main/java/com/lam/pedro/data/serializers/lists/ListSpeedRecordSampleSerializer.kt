package com.lam.pedro.data.serializers.lists

import android.util.Log
import androidx.health.connect.client.records.SpeedRecord
import com.lam.pedro.data.serializers.primitive.SpeedRecordSampleSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ListSpeedRecordSampleSerializer : KSerializer<List<SpeedRecord.Sample>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ListSample") {
        element("samples", ListSerializer(SpeedRecordSampleSerializer).descriptor)
    }

    override fun deserialize(decoder: Decoder): List<SpeedRecord.Sample> {
        try {
            return ListSerializer(SpeedRecordSampleSerializer).deserialize(decoder)
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing ListSample", e)
            return emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<SpeedRecord.Sample>) {
        try {
            ListSerializer(SpeedRecordSampleSerializer).serialize(encoder, value)
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ListSample", e)
        }
    }
}
