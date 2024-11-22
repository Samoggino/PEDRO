package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.data.activitySession.ActivitySession
import com.lam.pedro.data.activitySession.CycleSession
import com.lam.pedro.data.activitySession.DriveSession
import com.lam.pedro.data.activitySession.LiftSession
import com.lam.pedro.data.activitySession.ListenSession
import com.lam.pedro.data.activitySession.RunSession
import com.lam.pedro.data.activitySession.SitSession
import com.lam.pedro.data.activitySession.SleepSession
import com.lam.pedro.data.activitySession.TrainSession
import com.lam.pedro.data.activitySession.WalkSession
import com.lam.pedro.data.activitySession.YogaSession

@Composable
fun ShowSessionDetails(session: ActivitySession) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
            Text(text = "Dettagli della sessione:")
            Text(text = "Titolo: ${session.title}")
            Text(text = "Note: ${session.notes}")
            Text(text = "Inizio: ${session.startTime}")
            Text(text = "Fine: ${session.endTime}")
        }


        when (session) {
            is CycleSession -> {
                item {
                    Text(text = "Distanza: ${session.distance}")
                    Text(text = "Cadenza pedalata: ${session.cyclingPedalingCadenceSamples}")
                    Text(text = "Velocità: ${session.speedSamples}")
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Elevazione guadagnata: ${session.elevationGained}")
                }
            }
            is RunSession -> {
                item {
                    Text(text = "Velocità: ${session.speedSamples}")
                    Text(text = "Passi: ${session.stepsCount}")
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Distanza: ${session.distance}")
                    Text(text = "Elevazione guadagnata: ${session.elevationGained}")
                    Text(text = "Route: ${session.exerciseRoute}")
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
                    Text(text = "Cadenza passi: ${session.stepsCadenceSamples}")
                    Text(text = "Velocità media: ${session.speedSamples}")
                    Text(text = "Energia totale: ${session.totalEnergy}")
                    Text(text = "Energia attiva: ${session.activeEnergy}")
                    Text(text = "Elevazione guadagnata: ${session.elevationGained}")
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
                    Text(text = "Elevazione guadagnata: ${session.elevationGained}")
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
        }
    }
}
