package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import com.lam.pedro.presentation.theme.PedroYellow

@Composable
fun SessionHistoryRow(color: Color, image: Int, session: ExerciseSession, navController: NavController, viewModel: ActivitySessionViewModel) {

    Box(modifier = Modifier.clickable {
        navController.navigate(Screen.ExerciseSessionDetail.route) {
            viewModel.selectSession(session)
            navController.graph.startDestinationRoute?.let { route ->
                popUpTo(route) {
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = image),
                contentDescription = "Stop",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = session.title.toString(), modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )
        }

    }

}