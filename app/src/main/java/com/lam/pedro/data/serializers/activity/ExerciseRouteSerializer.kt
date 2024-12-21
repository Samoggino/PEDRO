package com.lam.pedro.data.serializers.activity

/**
ExerciseRoute(route: List<ExerciseRoute.Location>)
 * )
 *
 */
import android.util.Log
import androidx.health.connect.client.records.ExerciseRoute
import com.lam.pedro.data.serializers.primitive.LocationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ExerciseRouteSerializer : KSerializer<ExerciseRoute> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ExerciseRoute") {
            try {// Qui specifichiamo che "route" è una lista di Location
                element("route", ListSerializer(LocationSerializer).descriptor)
            } catch (e: Exception) {
                Log.e("Creation-serialization", "Error creating descriptor", e)
                throw SerializationException("Error creating descriptor", e)
            }
        }

    override fun serialize(encoder: Encoder, value: ExerciseRoute) {
        try {
            val compositeEncoder = encoder.beginStructure(descriptor)
            compositeEncoder.encodeSerializableElement(
                descriptor,
                0,
                ListSerializer(LocationSerializer),
                value.route
            )
            compositeEncoder.endStructure(descriptor)
        } catch (e: Exception) {
            Log.e("Serialization", "Error serializing ExerciseRoute", e)
            throw SerializationException("Error serializing ExerciseRoute", e)
        }
    }

    override fun deserialize(decoder: Decoder): ExerciseRoute {
        try {
            val dec = decoder.beginStructure(descriptor)
            var route: List<ExerciseRoute.Location> = emptyList()

            loop@ while (true) {
                when (val index = dec.decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    0 -> route = dec.decodeSerializableElement(
                        descriptor,
                        index,
                        ListSerializer(LocationSerializer)
                    )

                    else -> throw SerializationException("Unknown index $index")
                }
            }

            dec.endStructure(descriptor)
            return ExerciseRoute(route)
        } catch (e: Exception) {
            Log.e("Deserialization-serialization", "Error deserializing ExerciseRoute", e)
            throw SerializationException("Error deserializing ExerciseRoute", e)
        }

    }
}
