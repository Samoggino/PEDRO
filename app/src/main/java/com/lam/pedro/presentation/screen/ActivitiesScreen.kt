package com.lam.pedro.presentation.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.lam.pedro.R
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.DeniedPermissionDialog
import com.lam.pedro.presentation.navigation.Screen

@Composable
fun ActivitiesScreen(
    navController: NavHostController
) {
    val staticActivities = listOf(
        ActivityItem("Sleeping", painterResource(id = ActivityEnum.SLEEP.image), 90.dp, Screen.SleepSessions.route, ActivityEnum.SLEEP.color),
        ActivityItem("Driving", painterResource(id = ActivityEnum.DRIVE.image), 100.dp, Screen.DriveSessionScreen.route, ActivityEnum.DRIVE.color),
        ActivityItem("Sitting", painterResource(id = ActivityEnum.SIT.image), 90.dp, Screen.SitSessionScreen.route, ActivityEnum.SIT.color),
        ActivityItem("Lifting", painterResource(id = ActivityEnum.LIFT.image), 90.dp, Screen.WeightScreen.route, ActivityEnum.LIFT.color),
        ActivityItem("Listening", painterResource(id = ActivityEnum.LISTEN.image), 90.dp, Screen.ListenSessionScreen.route, ActivityEnum.LISTEN.color)
    )

    val dynamicActivities = listOf(
        ActivityItem("Running", painterResource(id = ActivityEnum.RUN.image), 90.dp, Screen.RunSessionScreen.route, ActivityEnum.RUN.color),
        ActivityItem("Walking", painterResource(id = ActivityEnum.WALK.image), 90.dp, Screen.WalkSessionScreen.route, ActivityEnum.WALK.color),
        ActivityItem("Yoga", painterResource(id = ActivityEnum.YOGA.image), 90.dp, Screen.YogaSessionScreen.route, ActivityEnum.YOGA.color),
        ActivityItem("Cycling", painterResource(id = ActivityEnum.CYCLING.image), 90.dp, Screen.CycleSessionScreen.route, ActivityEnum.CYCLING.color),
        ActivityItem("Training", painterResource(id = ActivityEnum.TRAIN.image), 90.dp, Screen.TrainSessionScreen.route, ActivityEnum.TRAIN.color)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Static Activities",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActivitiesGrid(navController, staticActivities)
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Dynamic Activities",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
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
    columns: Int = 2,
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
                    activity.route?.let {
                        ActivityCard(
                            activity = activity,
                            onClick = {
                                navController.navigate(activity.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }

                            },
                            modifier = Modifier.weight(1f) // Assegna il peso a ciascun elemento in una riga
                        )
                    }
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
    modifier: Modifier = Modifier
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
                .size(activity.iconSize)
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
    val icon: Painter,
    val iconSize: Dp,
    val route: String?,
    val color: Color
)