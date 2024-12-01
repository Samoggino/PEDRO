package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.CyclingSession
import com.lam.pedro.data.activity.RunSession
import com.lam.pedro.data.activity.WalkSession
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.presentation.serialization.ViewModelRecordFactory
import com.lam.pedro.presentation.serialization.ViewModelRecords
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties

@Composable
fun ScreenCharts(
    activityType: ActivityType,
    viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
) {
    val activities by viewModel.activitySessions.observeAsState(emptyList())
    val error by viewModel.error.observeAsState("")

    LaunchedEffect(true, error) {
        Log.d("Supabase", "Caricamento delle attività di tipo $activityType")
        viewModel.loadActivitySession(activityType)
    }



    Column {
        var barsList: List<Bars> = emptyList()
        // fai il cast al tipo di attività corretto
        when (ActivityType.CYCLING) {
            ActivityType.CYCLING -> {
                val sessions = activities.map { it as CyclingSession }

                // Creiamo una lista di `Bars` dinamicamente a partire dai dati
                barsList =
                    sessions
                        .groupBy { it.basicActivity.startTime.toMonthNumber() } // Raggruppiamo per mese (o altro criterio)
                        .toList() // Convertiamo in lista per poter usare `map`
                        .sortedBy { it.first } // Ordiniamo per mese
                        .map { (month, sessionsInMonth) ->
                            // Per ogni mese, creiamo una colonna
                            Bars(
                                label = month.toString(), // Usa il nome del mese o un altro criterio
                                values = listOf(
                                    Bars.Data(
                                        label = sessions[0].activityType.toString(), // Etichetta per la sessione
                                        value = sessionsInMonth.sumOf { it.distance.inMeters }, // Somma delle distanze per quel mese
                                        color = Brush.verticalGradient(
                                            colors = listOf(
                                                activityType.color,
                                                activityType.color
                                            ) // Gradiente per la colonna
                                        )
                                    )
                                )
                            )
                        }


            }

            ActivityType.RUN -> {
                val sessions = activities.map { it as RunSession }
            }

            ActivityType.WALK -> {
                val sessions = activities.map { it as WalkSession }
            }

            ActivityType.YOGA -> TODO()
            ActivityType.TRAIN -> TODO()
            ActivityType.DRIVE -> TODO()
            ActivityType.SIT -> TODO()
            ActivityType.SLEEP -> TODO()
            ActivityType.LIFT -> TODO()
            ActivityType.LISTEN -> TODO()

        }

        Chart(barsList)
    }

}

@Composable
private fun Chart(barsList: List<Bars>) {

    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = Color.White
    )

    ColumnChart(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        data = barsList,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = 20.dp
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        // righe di testo per colonna
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = textStyle,
        ),
        popupProperties = PopupProperties(
            textStyle = textStyle,
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = textStyle,
        )


    )
}
