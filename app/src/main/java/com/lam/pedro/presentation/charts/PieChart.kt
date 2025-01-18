package com.lam.pedro.presentation.charts

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseSupabaseRepositoryImpl
import com.lam.pedro.presentation.screen.community.user.UserCommunityDetailsViewModel
import com.lam.pedro.presentation.screen.community.user.UserCommunityDetailsViewModelFactory
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyPieChart(
    map: Map<ActivityEnum, List<GenericActivity>>,
    selectedUsername: String,
    backgroundColor: Color
) {
    // Calcolo dei dati per il grafico, escludendo attività vuote
    val data = map.mapNotNull { (activityEnum, activities) ->
        // Somma delle ore di attività
        val totalDuration = activities.sumOf { it.basicActivity.durationInHours() }
        if (totalDuration > 0) {
            Pie(
                label = activityEnum.name, // Nome dell'attività
                data = totalDuration,
                color = activityEnum.color, // Colore definito nell'enum
                selectedColor = activityEnum.color.copy(alpha = 0.5f) // Colore quando selezionato
            )
        } else null // Escludi attività con durata totale pari a 0
    }

    var pieData by remember { mutableStateOf(data) }
    var selectedActivity by remember { mutableStateOf<Pie?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // La sheet si apre completamente o si chiude
    )

    // Mostra il grafico
    Scaffold {
        if (pieData.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor), // Imposta lo sfondo come trasparente
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    modifier = Modifier.size(200.dp),
                    data = pieData,
                    onPieClick = {
                        selectedActivity = it // Salva l'attività cliccata
                        // Seleziona il grafico cliccato
                        val pieIndex = pieData.indexOf(it)
                        pieData =
                            pieData.mapIndexed { index, pie -> pie.copy(selected = pieIndex == index) }
                    },
                    selectedScale = 1.2f,
                    scaleAnimEnterSpec = spring<Float>(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    colorAnimEnterSpec = tween(300),
                    colorAnimExitSpec = tween(300),
                    scaleAnimExitSpec = tween(300),
                    spaceDegreeAnimExitSpec = tween(300),
                    spaceDegree = 7f,
                    selectedPaddingDegree = 4f,
                    style = Pie.Style.Stroke(width = 40.dp),
                )
            }

        } else {
            // Mostra un messaggio o un placeholder se non ci sono dati
            Text(
                text = "Nessuna attività registrata",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
    }

    // Mostra il ModalBottomSheet se un'attività è selezionata
    if (selectedActivity != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedActivity = null }, // Chiudi la sheet
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${selectedActivity?.label}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "$selectedUsername spent ${selectedActivity?.data?.toInt()} hours out of ${pieData.sumOf { it.data.toInt() }} total hours.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { selectedActivity = null }, // Chiudi la sheet
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(
                        text = "Chiudi",
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun MyPieChartButton(
    selectedUser: String = getUUID()!!,
    selectedUsername: String,
    chartBackgroundColor: Color
) {
    // lancia la funzione MyPieChart
    val viewModel: UserCommunityDetailsViewModel = viewModel(
        factory = UserCommunityDetailsViewModelFactory(
            activityRepository = ActivitySupabaseSupabaseRepositoryImpl(),
            userUUID = selectedUser
        )
    )

    val map = viewModel.activityMap.collectAsState()

    MyPieChart(
        map = map.value,
        selectedUsername = selectedUsername,
        backgroundColor = chartBackgroundColor
    )
}
