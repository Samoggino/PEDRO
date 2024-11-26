package com.lam.pedro.presentation.serialization

import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import androidx.health.connect.client.units.Volume
import com.lam.pedro.data.activity.ActivityInterface
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.BasicActivity
import com.lam.pedro.data.activity.CyclingSession
import com.lam.pedro.data.activity.DriveSession
import com.lam.pedro.data.activity.LiftSession
import com.lam.pedro.data.activity.ListenSession
import com.lam.pedro.data.activity.RunSession
import com.lam.pedro.data.activity.SitSession
import com.lam.pedro.data.activity.SleepSession
import com.lam.pedro.data.activity.TrainSession
import com.lam.pedro.data.activity.WalkSession
import com.lam.pedro.data.activity.YogaSession
import java.time.Instant
import java.time.ZoneOffset
import kotlin.reflect.KClass

object SessionCreator {

    val startTime: Instant = Instant.now()
    val endTime: Instant = Instant.now().plusSeconds(3600)
    val startZoneOffset: ZoneOffset? = ZoneOffset.UTC
    val endZoneOffset: ZoneOffset? = ZoneOffset.UTC
    val energy: Energy = Energy.kilocalories(100.0)
    val length: Length = Length.meters(500.0)

    fun createYogaSession(): YogaSession {
        return YogaSession(
            basicActivity = BasicActivity(
                startTime = Instant.now(),
                endTime = Instant.now(),
                title = "Yoga",
                notes = "Yoga session"
            ),
            totalEnergy = energy,
            activeEnergy = energy,
            exerciseSegment = listOf(
                ExerciseSegment(
                    startTime = Instant.now(),
                    endTime = Instant.now(),
                    segmentType = 0,
                    repetitions = 1
                )
            ),
            exerciseLap = listOf(
                ExerciseLap(
                    startTime = Instant.now(),
                    endTime = Instant.now(),
                    length = length
                )
            )
        )
    }

    fun createRunSession(): RunSession {
        return RunSession(
            basicActivity = BasicActivity(
                startTime = Instant.now(),
                endTime = Instant.now(),
                title = "Run",
                notes = "Run session"
            ),
            totalEnergy = energy,
            activeEnergy = energy,
            speedSamples = speedRecordSampleList(),
            stepsCount = 1000,
            distance = length,
            elevationGained = length,
            exerciseRoute = exerciseRouteCreator()
        )
    }

    private fun speedRecordSampleList() = listOf(
        SpeedRecord.Sample(
            time = Instant.now(),
            speed = Velocity.metersPerSecond(10.0)
        ),
        SpeedRecord.Sample(
            time = endTime,
            speed = Velocity.metersPerSecond(10.0)
        )
    )

    private fun exerciseRouteCreator() = ExerciseRoute(
        route = listOf(
            ExerciseRoute.Location(
                time = startTime,
                latitude = 0.0,
                longitude = 0.0,
                horizontalAccuracy = length,
                verticalAccuracy = length,
                altitude = length
            ),
            ExerciseRoute.Location(
                time = endTime,
                latitude = 0.0,
                longitude = 0.0,
                horizontalAccuracy = length,
                verticalAccuracy = length,
                altitude = length
            )
        )
    )

    fun listenSessionCreator() = ListenSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Listen",
            notes = "Listen session"
        )
    )

    fun liftSessionCreator() = LiftSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Lift",
            notes = "Lift session"
        ),
        totalEnergy = energy,
        activeEnergy = energy,
        exerciseSegment = listOf(
            ExerciseSegment(
                startTime = Instant.now(),
                endTime = Instant.now(),
                segmentType = 0,
                repetitions = 1
            )
        ),
        exerciseLap = listOf(
            ExerciseLap(
                startTime = Instant.now(),
                endTime = Instant.now(),
                length = length
            )
        )
    )

    fun sitSessionCreator() = SitSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Sit",
            notes = "Sit session"
        ),
        volume = Volume.liters(100.0)
    )

    fun cyclingSessionCreator() = CyclingSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Cycling",
            notes = "Cycling session"
        ),
        totalEnergy = energy,
        activeEnergy = energy,
        distance = length,
        elevationGained = length,
        exerciseRoute = exerciseRouteCreator(),
        speedSamples = speedRecordSampleList(),
        cyclingPedalingCadenceSamples = listOf(
            CyclingPedalingCadenceRecord.Sample(
                time = Instant.now(),
                revolutionsPerMinute = 1.0
            ),
            CyclingPedalingCadenceRecord.Sample(
                time = endTime,
                revolutionsPerMinute = 1.0
            )
        )
    )

    fun trainSessionCreator() = TrainSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Train",
            notes = "Train session"
        ),
        totalEnergy = energy,
        activeEnergy = energy,
        exerciseSegment = listOf(
            ExerciseSegment(
                startTime = Instant.now(),
                endTime = Instant.now(),
                segmentType = 0,
                repetitions = 1
            )
        ),
        exerciseLap = listOf(
            ExerciseLap(
                startTime = Instant.now(),
                endTime = Instant.now(),
                length = length
            )
        )
    )

    fun driveSessionCreator() = DriveSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Drive",
            notes = "Drive session"
        ),
        distance = length,
        elevationGained = length,
        exerciseRoute = exerciseRouteCreator(),
        speedSamples = listOf(
            SpeedRecord.Sample(
                time = Instant.now(),
                speed = Velocity.kilometersPerHour(10.0)
            ),
            SpeedRecord.Sample(
                time = endTime,
                speed = Velocity.kilometersPerHour(10.0)
            )
        )
    )

    fun sleepSessionCreator() = SleepSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Sleep",
            notes = "Sleep session"
        )
    )

    fun walkSessionCreator() = WalkSession(
        basicActivity = BasicActivity(
            startTime = Instant.now(),
            endTime = Instant.now(),
            title = "Walk",
            notes = "Walk session"
        ),
        totalEnergy = energy,
        activeEnergy = energy,
        distance = length,
        elevationGained = length,
        exerciseRoute = exerciseRouteCreator(),
        speedSamples = speedRecordSampleList(),
        stepsCount = 1000
    )

    data class ActivityConfig<T : ActivityInterface>(
        val responseType: KClass<T>,
        val sessionCreator: () -> T // Funzione per creare una nuova sessione
    )

    // Configura i tuoi tipi di attività
    val activityConfigs = mapOf(
        ActivityType.YOGA to ActivityConfig(
            responseType = YogaSession::class,
            sessionCreator = ::createYogaSession
        ),
        ActivityType.RUN to ActivityConfig(
            responseType = RunSession::class,
            sessionCreator = ::createRunSession
        ),
        ActivityType.LISTEN to ActivityConfig(
            responseType = ListenSession::class,
            sessionCreator = ::listenSessionCreator
        ),
        ActivityType.LIFT to ActivityConfig(
            responseType = LiftSession::class,
            sessionCreator = ::liftSessionCreator
        ),
        ActivityType.SIT to ActivityConfig(
            responseType = SitSession::class,
            sessionCreator = ::sitSessionCreator
        ),
        ActivityType.CYCLING to ActivityConfig(
            responseType = CyclingSession::class,
            sessionCreator = ::cyclingSessionCreator
        ),
        ActivityType.TRAIN to ActivityConfig(
            responseType = TrainSession::class,
            sessionCreator = ::trainSessionCreator
        ),
        ActivityType.DRIVE to ActivityConfig(
            responseType = DriveSession::class,
            sessionCreator = ::driveSessionCreator
        ),
        ActivityType.SLEEP to ActivityConfig(
            responseType = SleepSession::class,
            sessionCreator = ::sleepSessionCreator
        ),
        ActivityType.WALK to ActivityConfig(
            responseType = WalkSession::class,
            sessionCreator = ::walkSessionCreator
        )
    )

}