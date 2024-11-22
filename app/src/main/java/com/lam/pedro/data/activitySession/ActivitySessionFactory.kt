package com.lam.pedro.data.activitySession

import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Volume
import java.time.Instant

// Factory per creare le sessioni
object ActivitySessionFactory {
    private val exerciseRouteSamples = ExerciseRoute(listOf(ExerciseRoute.Location(Instant.now(), 1.0, 1.0)))
    //TODO: retrive exerciseRoute from Health Connect
    private val creators: Map<Int, (ExerciseSessionRecord) -> ActivitySession> = mapOf(

        ExerciseSessionRecord.EXERCISE_TYPE_BIKING to { record ->

            CycleSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Cycling Activity",
                notes = record.notes ?: "",
                speedSamples = emptyList(), // Non disponibile in ExerciseSessionRecord
                cyclingPedalingCadenceSamples = emptyList(), // Non disponibile
                totalEnergy = Energy.calories(0.0), // Fallback
                activeEnergy = Energy.calories(0.0), // Fallback
                distance = Length.meters(0.0), // Fallback
                elevationGained = Length.meters(0.0), // Fallback
                exerciseRoute = exerciseRouteSamples //Fallback
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT to { record ->
            DriveSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Driving Activity",
                notes = record.notes ?: "",
                speedSamples = emptyList(), // Non disponibile
                distance = Length.meters(0.0), // Fallback
                elevationGained = Length.meters(0.0), // Fallback
                exerciseRoute = exerciseRouteSamples //Fallback
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS to { record ->
            LiftSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Lift Activity",
                notes = record.notes ?: "",
                totalEnergy = Energy.calories(0.0),
                activeEnergy = Energy.calories(0.0),
                exerciseSegment = record.segments,
                exerciseLap = record.laps
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT to { record ->
            ListenSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Listening Activity",
                notes = record.notes ?: ""
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING to { record ->
            RunSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Running Activity",
                notes = record.notes ?: "",
                speedSamples = emptyList(), // Non disponibile
                stepsCount = 0L, // Fallback
                totalEnergy = Energy.calories(0.0), // Fallback
                activeEnergy = Energy.calories(0.0), // Fallback
                distance = Length.meters(0.0), // Fallback
                elevationGained = Length.meters(0.0), // Fallback
                exerciseRoute = exerciseRouteSamples //Fallback
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT to { record ->
            SitSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Sitting Activity",
                notes = record.notes ?: "",
                volume = Volume.liters(0.0) // Fallback
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT to { record ->
            SleepSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Sleep Activity",
                notes = record.notes ?: ""
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS to { record ->
            TrainSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Training Activity",
                notes = record.notes ?: "",
                totalEnergy = Energy.calories(0.0), // Fallback
                activeEnergy = Energy.calories(0.0), // Fallback
                exerciseSegment = record.segments, // Direttamente disponibile
                exerciseLap = record.laps // Direttamente disponibile
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING to { record ->
            WalkSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Walking Activity",
                notes = record.notes ?: "",
                speedSamples = emptyList(), // Non disponibile
                stepsCadenceSamples = emptyList(), // Non disponibile
                stepsCount = 0L, // Fallback
                totalEnergy = Energy.calories(0.0), // Fallback
                activeEnergy = Energy.calories(0.0), // Fallback
                distance = Length.meters(0.0), // Fallback
                elevationGained = Length.meters(0.0), // Fallback
                exerciseRoute = exerciseRouteSamples //Fallback
            )
        },
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA to { record ->
            YogaSession(
                startTime = record.startTime,
                endTime = record.endTime,
                title = record.title ?: "My Yoga Activity",
                notes = record.notes ?: "",
                totalEnergy = Energy.calories(0.0), // Fallback
                activeEnergy = Energy.calories(0.0), // Fallback
                exerciseSegment = record.segments, // Direttamente disponibile
                exerciseLap = record.laps // Direttamente disponibile
            )
        },



        // Aggiungi altre mappature se necessario
    )

    fun create(exerciseType: Int, record: ExerciseSessionRecord): ActivitySession {
        return creators[exerciseType]?.invoke(record)
            ?: throw IllegalArgumentException("Unsupported exercise type: $exerciseType")
    }
}
