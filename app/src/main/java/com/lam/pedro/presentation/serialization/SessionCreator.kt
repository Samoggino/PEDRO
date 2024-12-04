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
import com.lam.pedro.data.activity.ActivityType
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

    val startTime: Instant = Instant.now()
    val endTime: Instant = Instant.now().plusSeconds(3600)
    val energy: Energy = Energy.kilocalories(100.0)
    val length: Length = Length.meters(500.0)

    fun createYogaSession(): YogaSession {
        return YogaSession(
            basicActivity = basicActivity("Yoga Title", "Yoga Notes"),
            totalEnergy = energy,
            activeEnergy = energy,
            exerciseSegment = exerciseSegmentList(),
            exerciseLap = exerciseLapList()
        )
    }

    fun createRunSession(): RunSession {
        return RunSession(
            basicActivity = basicActivity("Run Title", "Run Notes"),
            totalEnergy = energy,
            activeEnergy = energy,
            speedSamples = speedRecordSampleList(),
            stepsCount = 1000,
            distance = length,
            elevationGained = length,
            exerciseRoute = exerciseRouteCreator()
        )
    }

    fun listenSessionCreator() = ListenSession(
        basicActivity = basicActivity("Listen Title", "Listen Notes")
    )

    fun liftSessionCreator() = LiftSession(
        basicActivity = basicActivity("Lift Title", "Lift Notes"),
        totalEnergy = energy,
        activeEnergy = energy,
        exerciseSegment = exerciseSegmentList(),
        exerciseLap = exerciseLapList()
    )

    fun sitSessionCreator() = SitSession(
        basicActivity = basicActivity("Sit Title", "Sit Notes"),
        volume = Volume.liters(100.0)
    )

    fun cyclingSessionCreator() = CyclingSession(
        basicActivity = basicActivity("Cycling Title", "Cycling Notes"),
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

    fun driveSessionCreator() = DriveSession(
        basicActivity = basicActivity("Drive Title", "Drive Notes"),
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
        basicActivity = basicActivity("Sleep Title", "Sleep Notes"),
    )

    fun walkSessionCreator() = WalkSession(
        basicActivity = basicActivity("Walk Title", "Walk Notes"),
        totalEnergy = energy,
        activeEnergy = energy,
        distance = length,
        elevationGained = length,
        exerciseRoute = exerciseRouteCreator(),
        speedSamples = speedRecordSampleList(),
        stepsCount = 1000
    )

    fun trainSessionCreator() = TrainSession(
        basicActivity = basicActivity("Train Title", "Train Notes"),
        totalEnergy = energy,
        activeEnergy = energy,
        exerciseSegment = exerciseSegmentList(),
        exerciseLap = exerciseLapList()
    )

    private fun basicActivity(title: String, notes: String): BasicActivity {
        // Crea un anno e un mese randomici
        val randomMonth =
            Month.entries[Random.nextInt(Month.entries.size)] // Scegli un mese casuale
        val randomYear = 2024 // Puoi decidere l'anno (es. 2024)

        // Scegli un giorno casuale del mese
        val randomDay = Random.nextInt(1, randomMonth.length(false) + 1)

        // Crea la data con un giorno, mese e anno casuali
        val localDate = LocalDate.of(randomYear, randomMonth, randomDay)

        // Converte LocalDate in Instant
        val startTime = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()

        return BasicActivity(
            startTime = startTime,
            endTime = startTime,  // Imposta endTime uguale a startTime, puoi modificarlo se necessario
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