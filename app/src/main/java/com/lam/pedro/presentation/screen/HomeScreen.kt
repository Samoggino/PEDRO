package com.lam.pedro.presentation.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.presentation.navigation.Screen

@Composable
fun HomeScreen(onProfileClick: () -> Unit) {

    Log.i("Reload", "HomeScreen loaded out")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(50.dp))
            Row() {
                Text(
                    text = "Welcome back!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        onProfileClick()
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.pedro_home),
                contentDescription = "Pedro Image",
                modifier = Modifier.fillMaxWidth().size(300.dp)
            )
            Text("Next days forecast", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            WeatherTabScreen()
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun WeatherTabScreen() {

    // Colori definiti come esempio
    val lightBlue = Color(0xFF4A90E2) // Azzurro chiaro
    val cloudyGray = Color(0xFF6884B0) // Grigio nuvoloso

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Primo rettangolo azzurro chiaro
        WeatherDayBox(
            color = lightBlue,
            day = "Sunny",
            iconId = R.drawable.sunny_icon
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Secondo rettangolo grigio nuvoloso
        WeatherDayBox(
            color = cloudyGray,
            day = "Cloudy",
            iconId = R.drawable.cloudy_icon
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Terzo rettangolo grigio.secondario
        WeatherDayBox(
            color = lightBlue,
            day = "Sunny",
            iconId = R.drawable.sunny_icon
        )
    }
}

@Composable
fun WeatherDayBox(modifier: Modifier = Modifier, color: Color, day: String, iconId: Int) {
    Column(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(color)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = "Meteo Icon",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = day, style = MaterialTheme.typography.bodyLarge, color = Color.White)
    }
}





