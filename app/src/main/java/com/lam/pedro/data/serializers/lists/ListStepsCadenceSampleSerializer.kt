package com.lam.pedro.data.serializers.lists

import androidx.health.connect.client.records.StepsCadenceRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import android.util.Log

object ListStepsCadenceSampleSerializer : KSerializer<List<StepsCadenceRecord.Sample>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ListStepsCadenceSample") {
        try {
            element("samples", ListSerializer(StepsCadenceSampleSerializer).descriptor)
        } catch (e: Exception) {
            Log.e("Serializing", "Error creating descriptor for ListStepsCadenceSample", e)
        }
    }

    override fun deserialize(decoder: Decoder): List<StepsCadenceRecord.Sample> {
        return try {
            ListSerializer(StepsCadenceSampleSerializer).deserialize(decoder)
        } catch (e: Exception) {
            Log.e("Serializing", "Error deserializing ListStepsCadenceSample", e)
            emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<StepsCadenceRecord.Sample>) {
        try {
            ListSerializer(StepsCadenceSampleSerializer).serialize(encoder, value)
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ListStepsCadenceSample", e)
        }
    }
}
