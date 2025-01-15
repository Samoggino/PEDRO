@file:UseSerializers(
    InstantSerializer::class,
    EnergySerializer::class,
    LengthSerializer::class,
    ListExerciseLapSerializer::class,
    ExerciseRouteSerializer::class,
    ExerciseSegmentSerializer::class,
    ExerciseLapSerializer::class,
    SpeedRecordSampleSerializer::class,
    VolumeSerializer::class,
)

package com.lam.pedro.data.activity

import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.lists.ListExerciseLapSerializer
import com.lam.pedro.data.serializers.lists.ListExerciseSegmentSerializer
import com.lam.pedro.data.serializers.lists.ListSpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.lists.SpeedRecordSampleSerializer
import com.lam.pedro.data.serializers.primitive.EnergySerializer
import com.lam.pedro.data.serializers.primitive.ExerciseSegmentSerializer
import com.lam.pedro.data.serializers.primitive.InstantSerializer
import com.lam.pedro.data.serializers.primitive.LengthSerializer
import com.lam.pedro.data.serializers.primitive.VolumeSerializer
import kotlinx.datetime.Month
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import kotlin.random.Random


fun Instant.toMonthNumber(zoneId: ZoneId = ZoneId.systemDefault()): Int {
    return this.atZone(zoneId).monthValue
}

fun Int.toMonthString(): String {
    return Month.entries[this - 1].name
}

@Serializable
sealed class GenericActivity(
    open val activityEnum: ActivityEnum
) {
    abstract val basicActivity: BasicActivity

    interface DistanceMetrics {
        val distance: Length
    }

    interface EnergyMetrics {
        val totalEnergy: Energy
        val activeEnergy: Energy
    }

    interface StaticMetric
    interface FullMetrics : DistanceMetrics, EnergyMetrics


    @Serializable
    data class BasicActivity(
        val startTime: Instant,
        val endTime: Instant,
        val title: String = "My Activity #${Random.nextInt(0, Int.MAX_VALUE)}",
        val notes: String
    ) {

        // Metodo per ottenere la durata in double (in ore, per esempio)
        fun durationInHours(): Double {
            val duration = Duration.between(startTime, endTime)
            return duration.toMinutes() / 60.0 // Converte la durata in minuti e la divide per 60 per ottenere le ore
        }

        // Metodo opzionale per ottenere la durata in minuti (se preferisci minuti)
        fun durationInMinutes(): Double {
            val duration = Duration.between(startTime, endTime)
            return duration.toMinutes().toDouble() // Restituisce la durata in minuti
        }
    }

    @Serializable
    data class WalkSession(
        override val basicActivity: BasicActivity,
        override val totalEnergy: Energy,
        override val activeEnergy: Energy,
//    @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        val stepsCount: Long,
        override val distance: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityEnum = ActivityEnum.WALK), FullMetrics

    @Serializable
    data class RunSession(
        override val basicActivity: BasicActivity,
        override val totalEnergy: Energy,
        override val activeEnergy: Energy,
        //  @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        val stepsCount: Long,
        override val distance: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityEnum = ActivityEnum.RUN), FullMetrics

    @Serializable
    data class CyclingSession(
        override val basicActivity: BasicActivity,
        override val totalEnergy: Energy,
        override val activeEnergy: Energy,

        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        //@Serializable(with = ListCyclingPedalingCadenceRecordSample::class) val cyclingPedalingCadenceSamples: List<@Contextual CyclingPedalingCadenceRecord.Sample>,
        override val distance: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityEnum = ActivityEnum.CYCLING), FullMetrics


    @Serializable
    data class LiftSession(
        override val basicActivity: BasicActivity,

        override val activeEnergy: Energy,
        override val totalEnergy: Energy,

        @Serializable(with = ListExerciseSegmentSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
        @Serializable(with = ListExerciseLapSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>,
    ) : GenericActivity(activityEnum = ActivityEnum.LIFT), EnergyMetrics

    @Serializable
    data class TrainSession(
        override val basicActivity: BasicActivity,
        override val totalEnergy: Energy,
        override val activeEnergy: Energy,

        @Serializable(with = ListExerciseSegmentSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
        @Serializable(with = ListExerciseLapSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>,
    ) : GenericActivity(activityEnum = ActivityEnum.TRAIN), EnergyMetrics

    @Serializable
    data class YogaSession(
        override val basicActivity: BasicActivity,
        override val totalEnergy: Energy,
        override val activeEnergy: Energy,

        @Serializable(with = ListExerciseSegmentSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
        @Serializable(with = ListExerciseLapSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>,
    ) : GenericActivity(activityEnum = ActivityEnum.YOGA), EnergyMetrics


    @Serializable
    data class DriveSession(
        override val basicActivity: BasicActivity,

        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        override val distance: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityEnum = ActivityEnum.DRIVE), DistanceMetrics


    @Serializable
    data class SleepSession(
        override val basicActivity: BasicActivity,
    ) : GenericActivity(activityEnum = ActivityEnum.SLEEP), StaticMetric

    @Serializable
    data class SitSession(
        override val basicActivity: BasicActivity,
        val volume: Volume
    ) : GenericActivity(activityEnum = ActivityEnum.SIT), StaticMetric

    @Serializable
    data class ListenSession(
        override val basicActivity: BasicActivity,
    ) : GenericActivity(activityEnum = ActivityEnum.LISTEN), StaticMetric

    @Serializable
    data class UnknownSession(
        override val basicActivity: BasicActivity,
    ) : GenericActivity(activityEnum = ActivityEnum.UNKNOWN), StaticMetric
}
