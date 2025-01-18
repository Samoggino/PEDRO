package com.lam.pedro.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.CyclingSession
import com.lam.pedro.data.activity.GenericActivity.DriveSession
import com.lam.pedro.data.activity.GenericActivity.LiftSession
import com.lam.pedro.data.activity.GenericActivity.ListenSession
import com.lam.pedro.data.activity.GenericActivity.RunSession
import com.lam.pedro.data.activity.GenericActivity.SitSession
import com.lam.pedro.data.activity.GenericActivity.SleepSession
import com.lam.pedro.data.activity.GenericActivity.TrainSession
import com.lam.pedro.data.activity.GenericActivity.UnknownSession
import com.lam.pedro.data.activity.GenericActivity.WalkSession
import com.lam.pedro.data.activity.GenericActivity.YogaSession
import com.lam.pedro.util.calculateAverageSpeed
import org.maplibre.android.geometry.LatLng
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@Composable
fun ShowSessionDetails(
    session: GenericActivity
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SessionHeader(session)
        }
        val details = mutableListOf<Pair<String, String>>()

        when (session) {
            is CyclingSession -> {
                item {
                    DistanceAndSpeedPrinter(session.distance, session.speedSamples, details)
                    EnergyPrinter(session.activeEnergy, session.totalEnergy, details)

                    SessionDetailsSection(details = details)
                    MapComponent(session.exerciseRoute, session.activityEnum.color)
                }
            }

            is RunSession -> {
                item {

                    DistanceAndSpeedPrinter(session.distance, session.speedSamples, details)
                    StepsPrinter(session.stepsCount, details)
                    EnergyPrinter(session.activeEnergy, session.totalEnergy, details)



                    SessionDetailsSection(details = details)
                    MapComponent(session.exerciseRoute, session.activityEnum.color)
                }
            }

            is TrainSession -> {
                item {
                    EnergyPrinter(session.activeEnergy, session.totalEnergy, details)
                    ExerciseSegmentPrinter(session.exerciseSegment, details)
                    SessionDetailsSection(details = details)
                }
            }

            is WalkSession -> {
                item {
                    DistanceAndSpeedPrinter(session.distance, session.speedSamples, details)
                    StepsPrinter(session.stepsCount, details)
                    EnergyPrinter(session.activeEnergy, session.totalEnergy, details)


                    SessionDetailsSection(details = details)
                    MapComponent(session.exerciseRoute, session.activityEnum.color)
                }
            }

            is YogaSession -> {
                item {
                    EnergyPrinter(session.activeEnergy, session.totalEnergy, details)
                    ExerciseSegmentPrinter(session.exerciseSegment, details)
                    SessionDetailsSection(details = details)
                }
            }

            is DriveSession -> {
                item {
                    DistanceAndSpeedPrinter(session.distance, session.speedSamples, details)


                    SessionDetailsSection(details = details)
                    MapComponent(session.exerciseRoute, session.activityEnum.color)
                }
            }

            is LiftSession -> {
                item {
                    EnergyPrinter(session.activeEnergy, session.totalEnergy, details)
                    ExerciseSegmentPrinter(session.exerciseSegment, details)

                    SessionDetailsSection(details = details)
                }
            }

            is ListenSession -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "This is a listening session.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            is SitSession -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Water: ${session.volume.inLiters}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            is SleepSession -> {
                //nothing else to display
            }

            is UnknownSession -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "This is an unknown session.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepsPrinter(
    steps: Long,
    details: MutableList<Pair<String, String>>
) {
    if (steps > 0) details.add("Steps" to "$steps")
}

@SuppressLint("DefaultLocale")
@Composable
private fun DistanceAndSpeedPrinter(
    distance: Length,
    speedRecordSample: List<SpeedRecord.Sample>,
    details: MutableList<Pair<String, String>>
) {
    if (distance.inKilometers > 0) details.add(
        "Distance" to "${String.format("%.2f", distance.inKilometers)} km"
    )

    if (calculateAverageSpeed(speedRecordSample) > 0) details.add(
        "Average speed" to "${String.format("%.2f", calculateAverageSpeed(speedRecordSample))} km/h"
    )
}

@Composable
private fun ExerciseSegmentPrinter(
    exerciseSegment: List<ExerciseSegment>,
    details: MutableList<Pair<String, String>>
) {
    if (exerciseSegment.sumOf { it.repetitions } > 0) {
        details.add("Repetitions" to "${exerciseSegment.sumOf { it.repetitions }}")
        exerciseSegment.forEachIndexed { index, segment ->
            details.add("Segment $index (${segment.segmentType}) Repetitions" to "${segment.repetitions}")
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun EnergyPrinter(
    active: Energy,
    total: Energy,
    details: MutableList<Pair<String, String>>
) {
    if (active.inKilocalories > 0) {
        details.add("Active Energy" to "${String.format("%.2f", active.inKilocalories)} kcal")
        details.add("Total Energy" to "${String.format("%.2f", total.inKilocalories)} kcal")
    } else details.add("Energy" to "${String.format("%.2f", total.inKilocalories)} kcal")
}

@Composable
private fun MapComponent(
    exerciseRoute: ExerciseRoute,
    sessionColor: Color
) {
    val positions = exerciseRoute.route.map { LatLng(it.latitude, it.longitude) }

    // positions: [LatLng [latitude=0.0, longitude=0.0, altitude=0.0], LatLng [latitude=0.0, longitude=0.0, altitude=0.0]]
    // se è tutto 0.0 non mostrare la mappa

    if (positions.isNotEmpty() && positions.any { it.latitude != 0.0 && it.longitude != 0.0 }) {
        Spacer(modifier = Modifier.height(16.dp))
        MapComponent(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(26.dp)),
            positions = positions,
            color = sessionColor
        )
    }
}

@Composable
fun SessionHeader(session: GenericActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Session details",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = session.activityEnum.color
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = session.basicActivity.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = session.basicActivity.notes.ifEmpty { "There is no note" },
                style = MaterialTheme.typography.bodyMedium
            )
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            Text(
                text = session.basicActivity.startTime.atZone(ZoneId.systemDefault())
                    .format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium
            )
            val duration =
                Duration.between(session.basicActivity.startTime, session.basicActivity.endTime)
            val formattedDuration = formatDuration(duration)
            Text(text = formattedDuration, style = MaterialTheme.typography.bodyLarge)

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            Text(
                text = "Start: ${
                    session.basicActivity.startTime.atZone(ZoneId.systemDefault())
                        .format(timeFormatter)
                }",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "End: ${
                    session.basicActivity.endTime.atZone(ZoneId.systemDefault())
                        .format(timeFormatter)
                }",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SessionDetailsSection(
    details: List<Pair<String, Any>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            details.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
