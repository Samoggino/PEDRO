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
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.BasicActivity
import com.lam.pedro.data.activity.GenericActivity.CyclingSession
import com.lam.pedro.data.activity.GenericActivity.DriveSession
import com.lam.pedro.data.activity.GenericActivity.LiftSession
import com.lam.pedro.data.activity.GenericActivity.ListenSession
import com.lam.pedro.data.activity.GenericActivity.RunSession
import com.lam.pedro.data.activity.GenericActivity.SitSession
import com.lam.pedro.data.activity.GenericActivity.SleepSession
import com.lam.pedro.data.activity.GenericActivity.TrainSession
import com.lam.pedro.data.activity.GenericActivity.WalkSession
import com.lam.pedro.data.activity.GenericActivity.YogaSession
import kotlinx.datetime.Month
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random
import kotlin.reflect.KClass

object SessionCreator {

    private val startTime: Instant = Instant.now()
    private val endTime: Instant = Instant.now().plusSeconds(3600)
    private val energy: Energy = Energy.kilocalories(100.0)
    private val length: Length = Length.meters(500.0)

    fun createYogaSession(): YogaSession {
        return YogaSession(
            basicActivity = createBasicActivity("Yoga Title", "Yoga Notes"),
            totalEnergy = energy,
            activeEnergy = energy,
            exerciseSegment = exerciseSegmentList(),
            exerciseLap = exerciseLapList()
        )
    }

    fun createRunSession(): RunSession {
        return RunSession(
            basicActivity = createBasicActivity("Run Title", "Run Notes"),
            totalEnergy = energy,
            activeEnergy = energy,
            speedSamples = speedRecordSampleList(),
            stepsCount = 1000,
            distance = length,
            elevationGained = length,
            exerciseRoute = exerciseRouteCreator()
        )
    }

    fun createListenSession() = ListenSession(
        basicActivity = createBasicActivity("Listen Title", "Listen Notes")
    )

    fun createLiftSession() = LiftSession(
        basicActivity = createBasicActivity("Lift Title", "Lift Notes"),
        totalEnergy = energy,
        activeEnergy = energy,
        exerciseSegment = exerciseSegmentList(),
        exerciseLap = exerciseLapList()
    )

    fun createSitSession() = SitSession(
        basicActivity = createBasicActivity("Sit Title", "Sit Notes"),
        volume = Volume.liters(100.0)
    )

    fun createCyclingSession() = CyclingSession(
        basicActivity = createBasicActivity("Cycling Title", "Cycling Notes"),
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

    fun createDriveSession() = DriveSession(
        basicActivity = createBasicActivity("Drive Title", "Drive Notes"),
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

    fun createSleepSession() = SleepSession(
        basicActivity = createBasicActivity("Sleep Title", "Sleep Notes"),
    )

    fun createWalkSession() = WalkSession(
        basicActivity = createBasicActivity("Walk Title", "Walk Notes"),
        totalEnergy = energy,
        activeEnergy = energy,
        distance = length,
        elevationGained = length,
        exerciseRoute = exerciseRouteCreator(),
        speedSamples = speedRecordSampleList(),
        stepsCount = 1000
    )

    fun createTrainSession() = TrainSession(
        basicActivity = createBasicActivity("Train Title", "Train Notes"),
        totalEnergy = energy,
        activeEnergy = energy,
        exerciseSegment = exerciseSegmentList(),
        exerciseLap = exerciseLapList()
    )

    private fun createBasicActivity(title: String, notes: String): BasicActivity {

        val randomMonth = Month.entries[Random.nextInt(Month.entries.size)]
        val randomDay = Random.nextInt(1, randomMonth.length(false) + 1)
        val startTime = LocalDate
            .of(2024, randomMonth, randomDay)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        // durata random ma non troppo lunga per evitare problemi con Instant
        val endTime = startTime.plusSeconds(3600 * 1 / Random.nextLong(1, 5))

        return BasicActivity(
            startTime = startTime,
            endTime = endTime,
            title = title,
            notes = notes
        )
    }

    private fun exerciseSegmentList() = listOf(
        ExerciseSegment(
            startTime = Instant.now(),
            endTime = Instant.now(),
            segmentType = 0,
            repetitions = 1
        )
    )

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

    private fun exerciseLapList() = listOf(
        ExerciseLap(
            startTime = Instant.now(),
            endTime = Instant.now(),
            length = length
        )
    )


    data class ActivityConfig<T : GenericActivity>(
        val responseType: KClass<T>,
        val sessionCreator: () -> T // Funzione per creare una nuova sessione
    )

    // Configura i tuoi tipi di attività
    val activityConfigs = mapOf(
        ActivityEnum.YOGA to ActivityConfig(
            responseType = YogaSession::class,
            sessionCreator = ::createYogaSession
        ),
        ActivityEnum.RUN to ActivityConfig(
            responseType = RunSession::class,
            sessionCreator = ::createRunSession
        ),
        ActivityEnum.LISTEN to ActivityConfig(
            responseType = ListenSession::class,
            sessionCreator = ::createListenSession
        ),
        ActivityEnum.LIFT to ActivityConfig(
            responseType = LiftSession::class,
            sessionCreator = ::createLiftSession
        ),
        ActivityEnum.SIT to ActivityConfig(
            responseType = SitSession::class,
            sessionCreator = ::createSitSession
        ),
        ActivityEnum.CYCLING to ActivityConfig(
            responseType = CyclingSession::class,
            sessionCreator = ::createCyclingSession
        ),
        ActivityEnum.TRAIN to ActivityConfig(
            responseType = TrainSession::class,
            sessionCreator = ::createTrainSession
        ),
        ActivityEnum.DRIVE to ActivityConfig(
            responseType = DriveSession::class,
            sessionCreator = ::createDriveSession
        ),
        ActivityEnum.SLEEP to ActivityConfig(
            responseType = SleepSession::class,
            sessionCreator = ::createSleepSession
        ),
        ActivityEnum.WALK to ActivityConfig(
            responseType = WalkSession::class,
            sessionCreator = ::createWalkSession
        )
    )

}