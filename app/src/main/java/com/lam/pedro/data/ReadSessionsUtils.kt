package com.lam.pedro.data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.activityFactoryHealthConnect.ActivitySessionFactoryFromHealthConnectProvider

suspend fun buildActivitySession(
    healthConnectClient: HealthConnectClient,
    exerciseRecord: ExerciseSessionRecord,
    exerciseType: Int
): GenericActivity? {
    return ActivitySessionFactoryFromHealthConnectProvider.createSession(exerciseType, healthConnectClient, exerciseRecord)
}

/*
suspend fun buildActivitySession(
    healthConnectClient: HealthConnectClient,
    exerciseRecord: ExerciseSessionRecord,
    exerciseType: Int
): ActivitySession? {

    val startTime = exerciseRecord.startTime
    val endTime = exerciseRecord.endTime
    val title = exerciseRecord.title
    val notes = exerciseRecord.notes

    if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING || exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_WALKING) {
        val distanceRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = DistanceRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val speedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = SpeedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val stepsRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val totalCaloriesBurnedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = TotalCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val fetchActiveCaloriesBurnedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records
        val activeCaloriesBurnedRecord = if (fetchActiveCaloriesBurnedRecord.isNotEmpty())
            fetchActiveCaloriesBurnedRecord[0]
        else
            null

        val exerciseRoute = if (exerciseRecord.exerciseRouteResult is ExerciseRouteResult.Data) {
            (exerciseRecord.exerciseRouteResult as ExerciseRouteResult.Data).exerciseRoute
        } else {
            null
        }

        if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING) {
            return RunSession(
                title = title ?: "My run #${exerciseRecord.hashCode()}",
                notes = notes ?: "",
                startTime = startTime,
                endTime = endTime,
                speedSamples = speedRecord.samples,
                stepsCount = stepsRecord.count,
                totalEnergy = totalCaloriesBurnedRecord.energy,
                activeEnergy = activeCaloriesBurnedRecord?.energy ?: Energy.calories(0.0),
                distance = distanceRecord.distance,
                exerciseRoute = exerciseRoute
            )
        } else {
            return WalkSession(
                title = title ?: "My walk #${exerciseRecord.hashCode()}",
                notes = notes ?: "",
                startTime = startTime,
                endTime = endTime,
                speedSamples = speedRecord.samples,
                stepsCount = stepsRecord.count,
                totalEnergy = totalCaloriesBurnedRecord.energy,
                activeEnergy = activeCaloriesBurnedRecord?.energy ?: Energy.calories(0.0),
                distance = distanceRecord.distance,
                exerciseRoute = exerciseRoute
            )
        }
    } else if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_BIKING) {
        val distanceRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = DistanceRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val speedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = SpeedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val totalCaloriesBurnedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = TotalCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val fetchActiveCaloriesBurnedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records
        val activeCaloriesBurnedRecord = if (fetchActiveCaloriesBurnedRecord.isNotEmpty())
            fetchActiveCaloriesBurnedRecord[0]
        else
            null

        val exerciseRoute = if (exerciseRecord.exerciseRouteResult is ExerciseRouteResult.Data) {
            (exerciseRecord.exerciseRouteResult as ExerciseRouteResult.Data).exerciseRoute
        } else {
            null
        }

        return CycleSession(
            title = title ?: "My Run #${exerciseRecord.hashCode()}",
            notes = notes ?: "",
            startTime = startTime,
            endTime = endTime,
            speedSamples = speedRecord.samples,
            totalEnergy = totalCaloriesBurnedRecord.energy,
            activeEnergy = activeCaloriesBurnedRecord?.energy ?: Energy.calories(0.0),
            distance = distanceRecord.distance,
            exerciseRoute = exerciseRoute
        )
    } else if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT) {
        val distanceRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = DistanceRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val speedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = SpeedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val exerciseRoute = if (exerciseRecord.exerciseRouteResult is ExerciseRouteResult.Data) {
            (exerciseRecord.exerciseRouteResult as ExerciseRouteResult.Data).exerciseRoute
        } else {
            null
        }
        return DriveSession(
            title = title ?: "My Run #${exerciseRecord.hashCode()}",
            notes = notes ?: "",
            startTime = startTime,
            endTime = endTime,
            speedSamples = speedRecord.samples,
            distance = distanceRecord.distance,
            exerciseRoute = exerciseRoute
        )
    } else if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT) {
        ListenSession(
            title = title ?: "My Listen #${exerciseRecord.hashCode()}",
            notes = notes ?: "",
            startTime = startTime,
            endTime = endTime
        )
    } else if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT) {
        SitSession(
            title = title ?: "My Sit #${exerciseRecord.hashCode()}",
            notes = notes ?: "",
            startTime = startTime,
            endTime = endTime,
            volume = Volume.liters(10.0), //TODO
        )
    } else if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS || exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_YOGA) {
        val totalCaloriesBurnedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = TotalCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records[0]

        val fetchActiveCaloriesBurnedRecord = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        ).records
        val activeCaloriesBurnedRecord = if (fetchActiveCaloriesBurnedRecord.isNotEmpty())
            fetchActiveCaloriesBurnedRecord[0]
        else
            null

        return if (exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS) {
            TrainSession(
                title = title ?: "My Train #${exerciseRecord.hashCode()}",
                notes = notes ?: "",
                startTime = startTime,
                endTime = endTime,
                totalEnergy = totalCaloriesBurnedRecord.energy,
                activeEnergy = activeCaloriesBurnedRecord.energy,
                exerciseSegment = exerciseRecord.segments,
                exerciseLap = exerciseRecord.laps
            )
        } else return YogaSession(
            title = title ?: "My Yoga #${exerciseRecord.hashCode()}",
            notes = notes ?: "",
            startTime = startTime,
            endTime = endTime,
            totalEnergy = totalCaloriesBurnedRecord.energy,
            activeEnergy = activeCaloriesBurnedRecord.energy,
            exerciseSegment = exerciseRecord.segments,
            exerciseLap = exerciseRecord.laps
        )
    } else {
        return null
    }
}

 */




