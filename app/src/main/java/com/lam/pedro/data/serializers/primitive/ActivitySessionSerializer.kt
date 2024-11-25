package com.lam.pedro.data.serializers.primitive

import android.util.Log
import com.lam.pedro.data.activity.ActivitySession
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

object ActivitySessionSerializer : KSerializer<ActivitySession> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ActivitySession") {
        try {
            element("startTime", InstantSerializer.descriptor)
            element("endTime", InstantSerializer.descriptor)
            element("title", String.serializer().descriptor)
            element("notes", String.serializer().descriptor)
        } catch (e: Exception) {
            Log.e("Creating", "Error creating ActivitySessionSerializer descriptor", e)
            throw SerializationException("Error creating ActivitySessionSerializer descriptor", e)
        }
    }

    override fun serialize(encoder: Encoder, value: ActivitySession) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.startTime)
                encodeSerializableElement(descriptor, 1, InstantSerializer, value.endTime)
                encodeStringElement(descriptor, 2, value.title)
                encodeStringElement(descriptor, 3, value.notes)
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error serializing ActivitySession", e)
            throw SerializationException("Error serializing ActivitySession", e)
        }
    }

    override fun deserialize(decoder: Decoder): ActivitySession {
        try {
            return decoder.decodeStructure(descriptor) {
                var startTime = Instant.now()
                var endTime = Instant.now()
                var title = ""
                var notes = ""

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> startTime = decodeSerializableElement(descriptor, 0, InstantSerializer)
                        1 -> endTime = decodeSerializableElement(descriptor, 1, InstantSerializer)
                        2 -> title = decodeStringElement(descriptor, 2)
                        3 -> notes = decodeStringElement(descriptor, 3)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }

                ActivitySession(
                    startTime = startTime,
                    endTime = endTime,
                    title = title,
                    notes = notes,
                )
            }
        } catch (e: Exception) {
            Log.e("Deserializing", "Error deserializing ActivitySession", e)
            throw SerializationException("Error deserializing ActivitySession", e)
        }
    }
}