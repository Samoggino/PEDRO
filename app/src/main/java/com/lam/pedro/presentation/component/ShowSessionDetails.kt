package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@Composable
fun ShowSessionDetails(
    session: GenericActivity,
    color: Color = session.activityEnum.color
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Session details",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = session.basicActivity.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = session.basicActivity.notes.ifEmpty { "There is no note" },
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${
                    session.basicActivity.startTime.atZone(ZoneId.systemDefault()).toLocalDate()
                }"
            )
            Spacer(modifier = Modifier.height(10.dp))
            val duration =
                Duration.between(session.basicActivity.startTime, session.basicActivity.endTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60
            Text(text = "$hours:$minutes:$seconds", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(10.dp))
            val zonedDateStartTime = session.basicActivity.startTime.atZone(ZoneId.systemDefault())
            val startHours = zonedDateStartTime.hour
            val startMinutes = zonedDateStartTime.minute
            val startSeconds = zonedDateStartTime.second
            Text(text = "Start: $startHours:$startMinutes:$startSeconds")
            val zonedDateEndTime = session.basicActivity.endTime.atZone(ZoneId.systemDefault())
            val endHours = zonedDateEndTime.hour
            val endMinutes = zonedDateEndTime.minute
            val endSeconds = zonedDateEndTime.second
            Text(text = "End: $endHours:$endMinutes:$endSeconds")
        }


        when (session) {
            is CyclingSession -> {
                item {
                    Text(text = "Distanza: ${session.distance}")
                    Text(text = "Velocità: ${session.speedSamples}")
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                }
            }

            is RunSession -> {
                item {
                    val positions =
                        session.exerciseRoute.route.map { LatLng(it.latitude, it.longitude) }

                    Text(text = "Average speed: ${calculateAverageSpeed(session.speedSamples)}")
                    Text(text = "Steps: ${session.stepsCount}")
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Distance: ${session.distance}")
                    //Text(text = "Elevazione guadagnata: ${session.elevationGained}")
                    MapComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(26.dp)),
                        positions = positions,
                        color = color
                    )
                }
            }

            is TrainSession -> {
                item {
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Segmenti di esercizio: ${session.exerciseSegment}")
                    Text(text = "Giri di esercizio: ${session.exerciseLap}")
                }
            }

            is WalkSession -> {
                item {
                    Text(text = "Distanza: ${session.distance}")
                    Text(text = "Passi: ${session.stepsCount}")
                    Text(text = "Velocità media: ${session.speedSamples}")
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                }
            }

            is YogaSession -> {
                item {
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Segmenti di esercizio: ${session.exerciseSegment}")
                    Text(text = "Giri di esercizio: ${session.exerciseLap}")
                }
            }

            is DriveSession -> {
                item {
                    Text(text = "Distanza: ${session.distance}")
                    Text(text = "Velocità media: ${session.speedSamples}")
                }
            }

            is LiftSession -> {
                item {
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Segmenti di esercizio: ${session.exerciseSegment}")
                    Text(text = "Giri di esercizio: ${session.exerciseLap}")
                }
            }

            is ListenSession -> {
                item {
                    Text(text = "Questa è una sessione di ascolto.")
                }
            }

            is SitSession -> {
                item {
                    Text(text = "Volume: ${session.volume}")
                }
            }

            is SleepSession -> {
                //nothing else to display
            }

            is UnknownSession -> {
                item {
                    Text(text = "Questa è una sessione sconosciuta.")
                }
            }
        }
    }
}