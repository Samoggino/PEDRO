package com.lam.pedro.data.serializers.primitive

import android.util.Log
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.units.Length
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import java.time.Instant

/***
 * Location(
 *     time: Instant,
 *     latitude: Double,
 *     longitude: Double,
 *     horizontalAccuracy: Length? = null,
 *     verticalAccuracy: Length? = null,
 *     altitude: Length? = null
 * )
 */

object LocationSerializer : KSerializer<ExerciseRoute.Location> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element("time", InstantSerializer.descriptor)
        element("latitude", Double.serializer().descriptor)
        element("longitude", Double.serializer().descriptor)
        element("horizontalAccuracy", LengthSerializer.descriptor)
        element("verticalAccuracy", LengthSerializer.descriptor)
        element("altitude", LengthSerializer.descriptor)

    }

    override fun deserialize(decoder: Decoder): ExerciseRoute.Location {
        val compositeInput = decoder.beginStructure(descriptor)
        var time: Instant? = null
        var latitude: Double? = null
        var longitude: Double? = null
        var horizontalAccuracy: Length? = null
        var verticalAccuracy: Length? = null
        var altitude: Length? = null

        loop@ while (true) {
            when (val index = compositeInput.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> time =
                    compositeInput.decodeSerializableElement(descriptor, 0, InstantSerializer)

                1 -> latitude =
                    compositeInput.decodeDoubleElement(descriptor, 1)

                2 -> longitude =
                    compositeInput.decodeDoubleElement(descriptor, 2)

                3 -> horizontalAccuracy =
                    compositeInput.decodeSerializableElement(descriptor, 3, LengthSerializer)

                4 -> verticalAccuracy =
                    compositeInput.decodeSerializableElement(descriptor, 4, LengthSerializer)

                5 -> altitude =
                    compositeInput.decodeSerializableElement(descriptor, 5, LengthSerializer)

                else -> throw SerializationException("Unexpected index $index")
            }
        }
        compositeInput.endStructure(descriptor)

        // Restituisci il tipo originale
        return ExerciseRoute.Location(
            time!!,
            latitude!!,
            longitude!!,
            horizontalAccuracy,
            verticalAccuracy,
            altitude
        )
    }

    override fun serialize(encoder: Encoder, value: ExerciseRoute.Location) {
        try {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, InstantSerializer, value.time)
                encodeDoubleElement(descriptor, 1, value.latitude)
                encodeDoubleElement(descriptor, 2, value.longitude)
                if (value.horizontalAccuracy != null) {
                    encodeSerializableElement(
                        descriptor, 3, LengthSerializer,
                        value.horizontalAccuracy!!
                    )
                }
                if (value.verticalAccuracy != null) {
                    encodeSerializableElement(
                        descriptor, 4, LengthSerializer,
                        value.verticalAccuracy!!
                    )
                }
                if (value.altitude != null) {
                    encodeSerializableElement(
                        descriptor, 5, LengthSerializer,
                        value.altitude!!
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("Serializing", "Error: ${e.message}")
        }
    }
}