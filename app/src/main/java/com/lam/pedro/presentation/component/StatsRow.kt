package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lam.pedro.R

@Composable
fun StatsColumn(icon: Int, unit: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Centra gli elementi nella colonna
        modifier = Modifier.width(100.dp) // Imposta una larghezza specifica
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 35.sp),
            color = color
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = unit,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 25.sp),
            color = color
        )
    }
}

/*
@Composable
fun StatsRow(steps: Float, speed: Double, distance: MutableState<Double>, color: Color, modifier: Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Aggiungi padding se necessario
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatsColumn(
            icon = R.drawable.steps_icon,
            unit = "",
            value = "${steps.toInt()}", // Mostra solo la parte intera
            color = color
        )
        Spacer(modifier = Modifier.weight(1f)) // Distribuisci lo spazio in modo uniforme
        StatsColumn(
            icon = R.drawable.speed_icon,
            unit = "(m/s)",
            value = String.format("%.2f", speed), // Arrotonda a 2 decimali
            color = color
        )
        Spacer(modifier = Modifier.weight(1f))
        StatsColumn(
            icon = R.drawable.distance_icon,
            unit = "(m)",
            value = String.format("%.2f", distance.value), // Arrotonda a 2 decimali
            color = color
        )
    }
}

@Composable
fun StatsRowWithoutSteps(speed: Double, distance: MutableState<Double>, color: Color, modifier: Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Aggiungi padding se necessario
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatsColumn(
            icon = R.drawable.speed_icon,
            unit = "(m/s)",
            value = String.format("%.2f", speed), // Arrotonda a 2 decimali
            color = color
        )
        Spacer(modifier = Modifier.weight(1f)) // Distribuisci lo spazio in modo uniforme
        StatsColumn(
            icon = R.drawable.distance_icon,
            unit = "(m)",
            value = String.format("%.2f", distance.value), // Arrotonda a 2 decimali
            color = color
        )
    }
}

 */

@Composable
fun StatsRow(
    steps: Float? = null, // Parametro opzionale
    speed: Double,
    distance: Double,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Aggiungi padding se necessario
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mostra il campo dei passi solo se `steps` è fornito
        steps?.let {
            StatsColumn(
                icon = R.drawable.steps_icon,
                unit = "",
                value = "${steps.toInt()}", // Mostra solo la parte intera
                color = color
            )
            Spacer(modifier = Modifier.weight(1f)) // Distribuisci lo spazio in modo uniforme
        }

        StatsColumn(
            icon = R.drawable.speed_icon,
            unit = "(m/s)",
            value = String.format("%.2f", speed), // Arrotonda a 2 decimali
            color = color
        )
        Spacer(modifier = Modifier.weight(1f))
        StatsColumn(
            icon = R.drawable.distance_icon,
            unit = "(m)",
            value = String.format("%.2f", distance), // Arrotonda a 2 decimali
            color = color
        )
    }
}