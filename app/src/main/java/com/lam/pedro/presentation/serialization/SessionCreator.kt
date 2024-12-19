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


    fun createYogaSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
    ): YogaSession {
        return YogaSession(
            basicActivity = createBasicActivity(title, notes, startTime, endTime),
            totalEnergy = totalEnergy,
            activeEnergy = activeEnergy,
            exerciseSegment = exerciseSegment,
            exerciseLap = exerciseLap
        )
    }

    fun createRunSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        stepsCount: Long,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        exerciseRoute: ExerciseRoute
    ): RunSession {
        return RunSession(
            basicActivity = createBasicActivity(title, notes, startTime, endTime),
            totalEnergy = totalEnergy,
            activeEnergy = activeEnergy,
            speedSamples = speedSamples,
            stepsCount = stepsCount,
            distance = distance,
            exerciseRoute = exerciseRoute
        )
    }

    fun createListenSession(
        title: String,
        notes: String,
        startTime: Instant,
        endTime: Instant,
    ) = ListenSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime)
    )

    fun createLiftSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
    ) = LiftSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
        totalEnergy = totalEnergy,
        activeEnergy = activeEnergy,
        exerciseSegment = exerciseSegment,
        exerciseLap = exerciseLap
    )

    fun createSitSession(
        title: String,
        notes: String,
        startTime: Instant,
        endTime: Instant,
        volume: Volume
    ) = SitSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
        volume = volume
    )

    fun createCyclingSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        exerciseRoute: ExerciseRoute
    ) = CyclingSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
        totalEnergy = totalEnergy,
        activeEnergy = activeEnergy,
        distance = distance,
        speedSamples = speedSamples,
        exerciseRoute = exerciseRoute
    )

    fun createDriveSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        distance: Length,
        exerciseRoute: ExerciseRoute
    ) = DriveSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
        distance = distance,
        exerciseRoute = exerciseRoute,
        speedSamples = speedSamples
    )


    fun createSleepSession(
        title: String,
        notes: String,
        startTime: Instant,
        endTime: Instant
    ) = SleepSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
    )

    fun createWalkSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        stepsCount: Long,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        exerciseRoute: ExerciseRoute
    ) = WalkSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
        totalEnergy = totalEnergy,
        activeEnergy = activeEnergy,
        speedSamples = speedSamples,
        stepsCount = stepsCount,
        distance = distance,
        exerciseRoute = exerciseRoute
    )

    fun createTrainSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
    ) = TrainSession(
        basicActivity = createBasicActivity(title, notes, startTime, endTime),
        totalEnergy = totalEnergy,
        activeEnergy = activeEnergy,
        exerciseSegment = exerciseSegment,
        exerciseLap = exerciseLap
    )

    private fun createBasicActivity(
        title: String,
        notes: String,
        startTime: Instant,
        endTime: Instant
    ): BasicActivity {

        return BasicActivity(
            startTime = startTime,
            endTime = endTime,
            title = title,
            notes = notes
        )
    }

    data class ActivityConfig<T : GenericActivity>(
        val responseType: KClass<T>,
        val sessionCreator: () -> T // Funzione per creare una nuova sessione
    )

    // Configura i tuoi tipi di attività
    /*
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

     */

}