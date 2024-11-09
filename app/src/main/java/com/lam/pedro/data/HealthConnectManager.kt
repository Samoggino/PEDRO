package com.lam.pedro.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.changes.Change
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseLap
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsCadenceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.InsertRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import com.lam.pedro.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.io.InvalidObjectException
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlin.reflect.KClass

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1

/** Demonstrates reading and writing from Health Connect. */
class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val healthConnectCompatibleApps by lazy {
        val intent = Intent("androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE")

        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )
        }

        packages.associate {
            val icon = try {
                context.packageManager.getApplicationIcon(it.activityInfo.packageName)
            } catch (e: NotFoundException) {
                null
            }
            val label = context.packageManager.getApplicationLabel(it.activityInfo.applicationInfo)
                .toString()
            it.activityInfo.packageName to
                    HealthConnectAppInfo(
                        packageName = it.activityInfo.packageName,
                        icon = icon,
                        appLabel = label
                    )
        }
    }

    var availability = mutableStateOf(SDK_UNAVAILABLE)
        private set

    fun checkAvailability() {
        availability.value = HealthConnectClient.getSdkStatus(context)
    }

    init {
        checkAvailability()
    }

    /**
     * Determines whether all the specified permissions are already granted. It is recommended to
     * call [PermissionController.getGrantedPermissions] first in the permissions flow, as if the
     * permissions are already granted then there is no need to request permissions via
     * [PermissionController.createRequestPermissionResultContract].
     */
    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    suspend fun revokeAllPermissions() {
        healthConnectClient.permissionController.revokeAllPermissions()
    }

    /*
    /**
     * Obtains a list of [ExerciseSessionRecord]s in a specified time frame. An Exercise Session Record is a
     * period of time given to an activity, that would make sense to a user, e.g. "Afternoon run"
     * etc. It does not necessarily mean, however, that the user was *running* for that entire time,
     * more that conceptually, this was the activity being undertaken.
     */
    suspend fun readExerciseSessions(start: Instant, end: Instant): List<ExerciseSessionRecord> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

     */


    /**
     * Writes an [ExerciseSessionRecord] to Health Connect, and additionally writes underlying data for
     * the session too, such as [StepsRecord], [DistanceRecord] etc.
     */
    suspend fun writeRunSession(
        start: ZonedDateTime,
        end: ZonedDateTime,
        title: String = "My Run #${Random.nextInt(0, 60)}",
        notes: String
    ): InsertRecordsResponse {

        return healthConnectClient.insertRecords(
            listOf(
                ExerciseSessionRecord(
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                    title = title
                ),
                StepsRecord(
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    count = (1000 + 1000 * Random.nextInt(3)).toLong()
                ),
                DistanceRecord(
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    distance = Length.meters((1000 + 100 * Random.nextInt(20)).toDouble())
                ),
                TotalCaloriesBurnedRecord(
                    startTime = start.toInstant(),
                    startZoneOffset = start.offset,
                    endTime = end.toInstant(),
                    endZoneOffset = end.offset,
                    energy = Energy.calories(140 + (Random.nextInt(20)) * 0.01)
                )
            )
        )
    }

    suspend fun insertExerciseSession(
        startTime: Instant,
        endTime: Instant,
        exerciseType: Int,
        title: String,
        notes: String
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = exerciseType,
            title = title,
            notes = notes
        )

        // Insert the record into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(exerciseSessionRecord)

            )
            println("Exercise session recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session: ${e.message}")
        }
    }

    suspend fun insertCycleSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        cyclingPedalingCadenceSamples: List<CyclingPedalingCadenceRecord.Sample>,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            title = title,
            notes = notes,
            exerciseRoute = exerciseRoute
        )

        val activeCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = activeEnergy
        )
        val distanceRecord = DistanceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            distance = distance
        )
        val elevationGainedRecord = ElevationGainedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            elevation = elevationGained
        )

        val cyclingPedalingCadenceRecord = CyclingPedalingCadenceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = cyclingPedalingCadenceSamples
        )

        val speedRecord = SpeedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = speedSamples
        )

        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = totalEnergy
        )

        // Insert the record into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    activeCaloriesBurnedRecord,
                    distanceRecord,
                    elevationGainedRecord,
                    speedRecord,
                    cyclingPedalingCadenceRecord,
                    totalCaloriesBurnedRecord
                )

            )
            println("Exercise session recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session: ${e.message}")
        }
    }

    suspend fun insertRunSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        //stepsCadenceSamples: List<StepsCadenceRecord.Sample>,
        stepsCount: Long,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
            title = title,
            notes = notes,
            exerciseRoute = exerciseRoute
        )

        // Create other records as needed
        val activeCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = activeEnergy
        )
        val distanceRecord = DistanceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            distance = distance
        )
        val elevationGainedRecord = ElevationGainedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            elevation = elevationGained
        )
        val speedRecord = SpeedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = speedSamples
        )
        /*
        val stepsCadenceRecord = StepsCadenceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = stepsCadenceSamples
        )

         */
        val stepsRecord = StepsRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            count = stepsCount
        )
        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = totalEnergy
        )

        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    activeCaloriesBurnedRecord,
                    distanceRecord,
                    elevationGainedRecord,
                    speedRecord,
                    //stepsCadenceRecord,
                    stepsRecord,
                    totalCaloriesBurnedRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertTrainSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS,
            title = title,
            notes = notes,
            segments = exerciseSegment,
            laps = exerciseLap
        )

        // Create other records as needed
        val activeCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = activeEnergy
        )
        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = totalEnergy
        )

        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    activeCaloriesBurnedRecord,
                    totalCaloriesBurnedRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertWalkSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        stepsCadenceSamples: List<StepsCadenceRecord.Sample>,
        stepsCount: Long,
        totalEnergy: Energy,
        activeEnergy: Energy,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
            title = title,
            notes = notes,
            exerciseRoute = exerciseRoute
        )

        // Create other records as needed
        val activeCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = activeEnergy
        )
        val distanceRecord = DistanceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            distance = distance
        )
        val elevationGainedRecord = ElevationGainedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            elevation = elevationGained
        )
        val speedRecord = SpeedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = speedSamples
        )
        val stepsCadenceRecord = StepsCadenceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = stepsCadenceSamples
        )
        val stepsRecord = StepsRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            count = stepsCount
        )
        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = totalEnergy
        )

        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    activeCaloriesBurnedRecord,
                    distanceRecord,
                    elevationGainedRecord,
                    speedRecord,
                    stepsCadenceRecord,
                    stepsRecord,
                    totalCaloriesBurnedRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertYogaSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_YOGA,
            title = title,
            notes = notes,
            segments = exerciseSegment,
            laps = exerciseLap
        )

        // Create other records as needed
        val activeCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = activeEnergy
        )
        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = totalEnergy
        )

        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    activeCaloriesBurnedRecord,
                    totalCaloriesBurnedRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertDriveSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        speedSamples: List<SpeedRecord.Sample>,
        distance: Length,
        elevationGained: Length,
        exerciseRoute: ExerciseRoute
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT,
            title = title,
            notes = notes,
            exerciseRoute = exerciseRoute
        )
        val distanceRecord = DistanceRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            distance = distance
        )
        val elevationGainedRecord = ElevationGainedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            elevation = elevationGained
        )
        val speedRecord = SpeedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            samples = speedSamples
        )


        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    distanceRecord,
                    elevationGainedRecord,
                    speedRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertLiftSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        totalEnergy: Energy,
        activeEnergy: Energy,
        exerciseSegment: List<ExerciseSegment>,
        exerciseLap: List<ExerciseLap>
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING,
            title = title,
            notes = notes,
            segments = exerciseSegment,
            laps = exerciseLap
        )

        // Create other records as needed
        val activeCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = activeEnergy
        )
        val totalCaloriesBurnedRecord = TotalCaloriesBurnedRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            energy = totalEnergy
        )

        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    activeCaloriesBurnedRecord,
                    totalCaloriesBurnedRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertListenSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT,
            title = title,
            notes = notes
        )


        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertSitSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String,
        volume: Volume
    ) {

        // Create the ExerciseSessionRecord
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT,
            title = title,
            notes = notes
        )
        val hydrationRecord = HydrationRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            volume = volume
        )


        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    exerciseSessionRecord,
                    hydrationRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    suspend fun insertSleepSession(
        startTime: Instant,
        endTime: Instant,
        title: String = "My Run #${Random.nextInt(0, Int.MAX_VALUE)}",
        notes: String
    ) {

        val sleepSessionRecord = SleepSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            title = title,
            notes = notes
        )

        // Insert the records into Health Connect
        try {
            healthConnectClient.insertRecords(
                listOf(
                    sleepSessionRecord
                )
            )
            println("Exercise session with route recorded successfully!")
        } catch (e: Exception) {
            println("Error recording exercise session with route: ${e.message}")
        }
    }

    /**
     * Deletes an [ExerciseSessionRecord] and underlying data.
     */
    suspend fun deleteExerciseSession(uid: String) {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        healthConnectClient.deleteRecords(
            ExerciseSessionRecord::class,
            recordIdsList = listOf(uid),
            clientRecordIdsList = emptyList()
        )
        val timeRangeFilter = TimeRangeFilter.between(
            exerciseSession.record.startTime,
            exerciseSession.record.endTime
        )
        val rawDataTypes: Set<KClass<out Record>> = setOf(
            HeartRateRecord::class,
            SpeedRecord::class,
            DistanceRecord::class,
            StepsRecord::class,
            TotalCaloriesBurnedRecord::class
        )
        rawDataTypes.forEach { rawType ->
            healthConnectClient.deleteRecords(rawType, timeRangeFilter)
        }
    }

    // Funzione per leggere le sessioni di esercizio, con un filtro per exerciseType
    suspend fun readExerciseSessions(
        start: Instant,
        end: Instant,
        exerciseType: Int
    ): List<ExerciseSessionRecord> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )

        // Leggi i record dalle API
        val response = healthConnectClient.readRecords(request)

        // Filtra i record in base all'exerciseType
        return response.records.filter { record ->
            record.exerciseType == exerciseType
        }
    }


    /**
     * Reads aggregated data and raw data for selected data types, for a given [ExerciseSessionRecord].
     */
    suspend fun readAssociatedSessionData(
        uid: String
    ): ExerciseSessionData {
        val exerciseSession = healthConnectClient.readRecord(ExerciseSessionRecord::class, uid)
        // Use the start time and end time from the session, for reading raw and aggregate data.
        val timeRangeFilter = TimeRangeFilter.between(
            startTime = exerciseSession.record.startTime,
            endTime = exerciseSession.record.endTime
        )
        val aggregateDataTypes = setOf(
            ExerciseSessionRecord.EXERCISE_DURATION_TOTAL,
            StepsRecord.COUNT_TOTAL,
            DistanceRecord.DISTANCE_TOTAL,
            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            HeartRateRecord.BPM_AVG,
            HeartRateRecord.BPM_MAX,
            HeartRateRecord.BPM_MIN,
        )
        // Limit the data read to just the application that wrote the session. This may or may not
        // be desirable depending on the use case: In some cases, it may be useful to combine with
        // data written by other apps.
        val dataOriginFilter = setOf(exerciseSession.record.metadata.dataOrigin)
        val aggregateRequest = AggregateRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = timeRangeFilter,
            dataOriginFilter = dataOriginFilter
        )
        val aggregateData = healthConnectClient.aggregate(aggregateRequest)

        return ExerciseSessionData(
            uid = uid,
            totalActiveTime = aggregateData[ExerciseSessionRecord.EXERCISE_DURATION_TOTAL],
            totalSteps = aggregateData[StepsRecord.COUNT_TOTAL],
            totalDistance = aggregateData[DistanceRecord.DISTANCE_TOTAL],
            totalEnergyBurned = aggregateData[TotalCaloriesBurnedRecord.ENERGY_TOTAL],
            minHeartRate = aggregateData[HeartRateRecord.BPM_MIN],
            maxHeartRate = aggregateData[HeartRateRecord.BPM_MAX],
            avgHeartRate = aggregateData[HeartRateRecord.BPM_AVG],
        )
    }

    /**
     * Deletes all existing sleep data.
     */
    suspend fun deleteAllSleepData() {
        val now = Instant.now()
        healthConnectClient.deleteRecords(SleepSessionRecord::class, TimeRangeFilter.before(now))
    }

    /**
     * Generates a week's worth of sleep data using a [SleepSessionRecord] to describe the overall
     * period of sleep, with multiple [SleepSessionRecord.Stage] periods which cover the entire
     * [SleepSessionRecord]. For the purposes of this sample, the sleep stage data is generated randomly.
     */
    suspend fun generateSleepData() {
        val records = mutableListOf<Record>()
        // Make yesterday the last day of the sleep data
        val lastDay = ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS)
        val notes = context.resources.getStringArray(R.array.sleep_notes_array)
        // Create 7 days-worth of sleep data
        for (i in 0..7) {
            val wakeUp = lastDay.minusDays(i.toLong())
                .withHour(Random.nextInt(7, 10))
                .withMinute(Random.nextInt(0, 60))
            val bedtime = wakeUp.minusDays(1)
                .withHour(Random.nextInt(19, 22))
                .withMinute(Random.nextInt(0, 60))
            val sleepSession = SleepSessionRecord(
                notes = notes[Random.nextInt(0, notes.size)],
                startTime = bedtime.toInstant(),
                startZoneOffset = bedtime.offset,
                endTime = wakeUp.toInstant(),
                endZoneOffset = wakeUp.offset,
                stages = generateSleepStages(bedtime, wakeUp)
            )
            records.add(sleepSession)
        }
        healthConnectClient.insertRecords(records)
    }

    /**
     * Reads sleep sessions for the previous seven days (from yesterday) to show a week's worth of
     * sleep data.
     *
     * In addition to reading [SleepSessionRecord]s, for each session, the duration is calculated to
     * demonstrate aggregation, and the underlying [SleepSessionRecord.Stage] data is also read.
     */
    suspend fun readSleepSessions(): List<SleepSessionData> {
        val lastDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            .minusDays(1)
            .withHour(12)
        val firstDay = lastDay
            .minusDays(7)

        val sessions = mutableListOf<SleepSessionData>()
        val sleepSessionRequest = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(firstDay.toInstant(), lastDay.toInstant()),
            ascendingOrder = false
        )
        val sleepSessions = healthConnectClient.readRecords(sleepSessionRequest)
        sleepSessions.records.forEach { session ->
            val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
            val durationAggregateRequest = AggregateRequest(
                metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                timeRangeFilter = sessionTimeFilter
            )
            val aggregateResponse = healthConnectClient.aggregate(durationAggregateRequest)
            sessions.add(
                SleepSessionData(
                    uid = session.metadata.id,
                    title = session.title,
                    notes = session.notes,
                    startTime = session.startTime,
                    startZoneOffset = session.startZoneOffset,
                    endTime = session.endTime,
                    endZoneOffset = session.endZoneOffset,
                    duration = aggregateResponse[SleepSessionRecord.SLEEP_DURATION_TOTAL],
                    stages = session.stages
                )
            )
        }
        return sessions
    }

    suspend fun writeSleepSession(sessionData: SleepSessionData) {
        val sleepSessionRecord = SleepSessionRecord(
            startTime = sessionData.startTime,
            startZoneOffset = sessionData.startZoneOffset,
            endTime = sessionData.endTime,
            endZoneOffset = sessionData.endZoneOffset,
            title = sessionData.title,
            notes = sessionData.notes,
            stages = sessionData.stages // Aggiungi gli stadi della sessione, se presenti
        )

        try {
            healthConnectClient.insertRecords(listOf(sleepSessionRecord))
            Log.d("SleepSession", "Sessione di sonno aggiunta con successo!")
        } catch (e: Exception) {
            Log.e("SleepSession", "Errore nell'inserimento della sessione di sonno: ${e.message}")
            throw e
        }
    }


    /**
     * Writes [WeightRecord] to Health Connect.
     */
    suspend fun writeWeightInput(weight: WeightRecord) {
        val records = listOf(weight)
        healthConnectClient.insertRecords(records)
    }

    /**
     * Reads in existing [WeightRecord]s.
     */
    suspend fun readWeightInputs(start: Instant, end: Instant): List<WeightRecord> {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    /**
     * Returns the weekly average of [WeightRecord]s.
     */
    suspend fun computeWeeklyAverage(start: Instant, end: Instant): Mass? {
        val request = AggregateRequest(
            metrics = setOf(WeightRecord.WEIGHT_AVG),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.aggregate(request)
        return response[WeightRecord.WEIGHT_AVG]
    }

    /**
     * Deletes a [WeightRecord]s.
     */
    suspend fun deleteWeightInput(uid: String) {
        healthConnectClient.deleteRecords(
            WeightRecord::class,
            recordIdsList = listOf(uid),
            clientRecordIdsList = emptyList()
        )
    }

    /**
     * Obtains a changes token for the specified record types.
     */
    suspend fun getChangesToken(dataTypes: Set<KClass<out Record>>): String {
        val request = ChangesTokenRequest(dataTypes)
        return healthConnectClient.getChangesToken(request)
    }

    /**
     * Creates a [Flow] of change messages, using a changes token as a start point. The flow will
     * terminate when no more changes are available, and the final message will contain the next
     * changes token to use.
     */
    suspend fun getChanges(token: String): Flow<ChangesMessage> = flow {
        var nextChangesToken = token
        do {
            val response = healthConnectClient.getChanges(nextChangesToken)
            if (response.changesTokenExpired) {
                // As described here: https://developer.android.com/guide/health-and-fitness/health-connect/data-and-data-types/differential-changes-api
                // tokens are only valid for 30 days. It is important to check whether the token has
                // expired. As well as ensuring there is a fallback to using the token (for example
                // importing data since a certain date), more importantly, the app should ensure
                // that the changes API is used sufficiently regularly that tokens do not expire.
                throw IOException("Changes token has expired")
            }
            emit(ChangesMessage.ChangeList(response.changes))
            nextChangesToken = response.nextChangesToken
        } while (response.hasMore)
        emit(ChangesMessage.NoMoreChanges(nextChangesToken))
    }

    /** Creates a random sleep stage that spans the specified [start] to [end] time. */
    private fun generateSleepStages(
        start: ZonedDateTime,
        end: ZonedDateTime
    ): List<SleepSessionRecord.Stage> {
        val sleepStages = mutableListOf<SleepSessionRecord.Stage>()
        var stageStart = start
        while (stageStart < end) {
            val stageEnd = stageStart.plusMinutes(Random.nextLong(30, 120))
            val checkedEnd = if (stageEnd > end) end else stageEnd
            sleepStages.add(
                SleepSessionRecord.Stage(
                    stage = randomSleepStage(),
                    startTime = stageStart.toInstant(),
                    endTime = checkedEnd.toInstant()
                )
            )
            stageStart = checkedEnd
        }
        return sleepStages
    }

    /**
     * Convenience function to fetch a time-based record and return series data based on the record.
     * Record types compatible with this function must be declared in the
     * [com.example.healthconnectsample.presentation.screen.recordlist.RecordType] enum.
     */
    suspend fun fetchSeriesRecordsFromUid(
        recordType: KClass<out Record>,
        uid: String,
        seriesRecordsType: KClass<out Record>
    ): List<Record> {
        val recordResponse = healthConnectClient.readRecord(recordType, uid)
        // Use the start time and end time from the session, for reading raw and aggregate data.
        val timeRangeFilter =
            when (recordResponse.record) {
                // Change to use series record instead
                is ExerciseSessionRecord -> {
                    val record = recordResponse.record as ExerciseSessionRecord
                    TimeRangeFilter.between(startTime = record.startTime, endTime = record.endTime)
                }

                is SleepSessionRecord -> {
                    val record = recordResponse.record as SleepSessionRecord
                    TimeRangeFilter.between(startTime = record.startTime, endTime = record.endTime)
                }

                else -> {
                    throw InvalidObjectException("Record with unregistered data type returned")
                }
            }

        // Limit the data read to just the application that wrote the session. This may or may not
        // be desirable depending on the use case: In some cases, it may be useful to combine with
        // data written by other apps.
        val dataOriginFilter = setOf(recordResponse.record.metadata.dataOrigin)
        val request =
            ReadRecordsRequest(
                recordType = seriesRecordsType,
                dataOriginFilter = dataOriginFilter,
                timeRangeFilter = timeRangeFilter
            )
        return healthConnectClient.readRecords(request).records
    }

    // Represents the two types of messages that can be sent in a Changes flow.
    sealed class ChangesMessage {
        data class NoMoreChanges(val nextChangesToken: String) : ChangesMessage()

        data class ChangeList(val changes: List<Change>) : ChangesMessage()
    }
}