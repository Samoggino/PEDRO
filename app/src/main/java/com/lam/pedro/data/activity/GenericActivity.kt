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

import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.serializers.activity.ExerciseLapSerializer
import com.lam.pedro.data.serializers.activity.ExerciseRouteSerializer
import com.lam.pedro.data.serializers.lists.ListCyclingPedalingCadenceRecordSample
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
    open val activityType: ActivityType
) {
    abstract val basicActivity: BasicActivity

    interface DistanceMetrics
    interface EnergyMetrics


    @Serializable
    data class BasicActivity(
        val startTime: Instant,
        val endTime: Instant,
        val title: String = "My Activity #${Random.nextInt(0, Int.MAX_VALUE)}",
        val notes: String
    )

    @Serializable
    data class YogaSession(
        override val basicActivity: BasicActivity,
        val totalEnergy: Energy,
        val activeEnergy: Energy,

        @Serializable(with = ListExerciseSegmentSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
        @Serializable(with = ListExerciseLapSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>,
    ) : GenericActivity(activityType = ActivityType.YOGA), EnergyMetrics


    @Serializable
    data class WalkSession(
        override val basicActivity: BasicActivity,
        val totalEnergy: Energy,
        val activeEnergy: Energy,
//    @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        val stepsCount: Long,
        val distance: Length,
        val elevationGained: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityType = ActivityType.WALK), DistanceMetrics, EnergyMetrics


    @Serializable
    data class TrainSession(
        override val basicActivity: BasicActivity,
        val totalEnergy: Energy,
        val activeEnergy: Energy,

        @Serializable(with = ListExerciseSegmentSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
        @Serializable(with = ListExerciseLapSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>,
    ) : GenericActivity(activityType = ActivityType.TRAIN), EnergyMetrics

    @Serializable
    data class RunSession(
        override val basicActivity: BasicActivity,
        val totalEnergy: Energy,
        val activeEnergy: Energy,
        //  @Serializable(with = ListStepsCadenceSampleSerializer::class) val cadenceRecord: List<@Contextual StepsCadenceRecord.Sample>,
        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        val stepsCount: Long,
        val distance: Length,
        val elevationGained: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityType = ActivityType.RUN), DistanceMetrics, EnergyMetrics

    @Serializable
    data class DriveSession(
        override val basicActivity: BasicActivity,

        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        val distance: Length,
        val elevationGained: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityType = ActivityType.DRIVE), DistanceMetrics


    @Serializable
    data class CyclingSession(
        override val basicActivity: BasicActivity,
        val totalEnergy: Energy,
        val activeEnergy: Energy,

        @Serializable(with = ListSpeedRecordSampleSerializer::class) val speedSamples: List<@Contextual SpeedRecord.Sample>,
        @Serializable(with = ListCyclingPedalingCadenceRecordSample::class) val cyclingPedalingCadenceSamples: List<@Contextual CyclingPedalingCadenceRecord.Sample>,
        val distance: Length,
        val elevationGained: Length,
        val exerciseRoute: ExerciseRoute,
    ) : GenericActivity(activityType = ActivityType.CYCLING), DistanceMetrics, EnergyMetrics

    @Serializable
    data class SleepSession(
        override val basicActivity: BasicActivity,
    ) : GenericActivity(ActivityType.SLEEP)

    @Serializable
    data class SitSession(
        override val basicActivity: BasicActivity,
        val volume: Volume
    ) : GenericActivity(activityType = ActivityType.SIT)

    @Serializable
    data class ListenSession(
        override val basicActivity: BasicActivity,
    ) : GenericActivity(activityType = ActivityType.LISTEN)

    @Serializable
    data class LiftSession(
        override val basicActivity: BasicActivity,

        val activeEnergy: Energy,
        val totalEnergy: Energy,

        @Serializable(with = ListExerciseSegmentSerializer::class) val exerciseSegment: List<@Contextual ExerciseSegment>,
        @Serializable(with = ListExerciseLapSerializer::class) val exerciseLap: List<@Contextual ExerciseLap>,
    ) : GenericActivity(activityType = ActivityType.LIFT), EnergyMetrics

}
