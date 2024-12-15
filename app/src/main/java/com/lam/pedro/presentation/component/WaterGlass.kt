package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R

@Composable
fun WaterGlass(hydrationVolume: Double, onAddHydration: (Double) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Aggiungi padding se necessario
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .size(120.dp), // Dimensioni del contenitore
            contentAlignment = Alignment.Center // Allinea il "+" al centro
        ) {
            // Immagine del bicchiere
            Image(
                painter = painterResource(R.drawable.water_glass_icon),
                contentDescription = "Water Glass",
                modifier = Modifier.fillMaxSize(), // Riempie tutto il contenitore
                contentScale = ContentScale.Fit // Adatta l'immagine
            )

            // Simbolo "+"
            Icon(
                modifier = Modifier
                    .clickable {
                        onAddHydration(500.0)
                    }
                    .size(30.dp), // Dimensioni dell'icona,
                imageVector = Icons.Default.Add, // Usa l'icona predefinita di aggiunta
                contentDescription = "Add", // Descrizione per l'accessibilità
                tint = Color.White // Colore dell'icona
            )


        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = ("${hydrationVolume.toInt()} ml"),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
