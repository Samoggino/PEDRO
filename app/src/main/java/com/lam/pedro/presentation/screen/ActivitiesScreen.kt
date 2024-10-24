package com.lam.pedro.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.ChairAlt
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lam.pedro.presentation.navigation.Screen

@Composable
fun ActivitiesScreen(
    navController: NavHostController
) {
    val staticActivities = listOf(
        ActivityItem("Sleep", Icons.Filled.Bed, Screen.SleepSessions.route, Color(0xff74c9c6)),
        ActivityItem(
            "Drive",
            Icons.Filled.DirectionsCar,
            Screen.SleepSessions.route,
            Color(0xFF61a6f1)
        ),
        ActivityItem("Sit", Icons.Filled.ChairAlt, Screen.SleepSessions.route, Color(0xff71c97b)),
        ActivityItem("Weight", Icons.Filled.Album, Screen.InputReadings.route, Color(0xFF7771C9)),
        ActivityItem("Listen", Icons.Filled.Headphones, null, Color(0xFF71A9C9))
    )

    val dynamicActivities = listOf(
        ActivityItem(
            "Run",
            Icons.Filled.DirectionsRun,
            Screen.ExerciseSessions.route,
            Color(0xFFf87757)
        ),
        ActivityItem("Walk", Icons.Filled.DirectionsWalk, null, Color(0xFFfaaf5a)),
        ActivityItem("Yoga", Icons.Filled.SportsGymnastics, null, Color(0xFFad71c9)),
        ActivityItem("Cycling", Icons.Filled.DirectionsBike, null, Color(0xFFC2C971)),
        ActivityItem("Free Body", Icons.Filled.AirlineSeatReclineExtra, null, Color(0xFFC9B471))
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)/*.verticalScroll(rememberScrollState())*/
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Static Activities", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            ActivitiesGrid(navController, staticActivities)
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Dynamic Activities", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            ActivitiesGrid(navController, dynamicActivities)
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

}

@Composable
fun ActivitiesGrid(
    navController: NavHostController,
    activities: List<ActivityItem>,
    columns: Int = 2 // Numero di colonne per riga
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val rows = activities.chunked(columns)

        // Iteriamo sulle righe per creare una Row per ogni gruppo di colonne
        rows.forEach { rowActivities ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowActivities.forEach { activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = {
                            if (activity.route != null) {
                                navController.navigate(activity.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.weight(1f) // Assegna il peso a ciascun elemento in una riga
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                // Aggiungi un Box vuoto se ci sono colonne vuote
                val emptyCount = columns - rowActivities.size
                repeat(emptyCount) {
                    Box(modifier = Modifier.weight(1f)) // Box vuoto per occupare spazio
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}

@Composable
fun ActivityCard(
    activity: ActivityItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Aggiungi un parametro per passare il modificatore
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(activity.color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomStart
    ) {
        Icon(
            activity.icon,
            contentDescription = null,
            tint = Color(0x33FFFFFF),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(90.dp)
        )
        Text(
            text = activity.name,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(10.dp)
        )
    }
}


data class ActivityItem(
    val name: String,
    val icon: ImageVector,
    val route: String?,
    val color: Color
)