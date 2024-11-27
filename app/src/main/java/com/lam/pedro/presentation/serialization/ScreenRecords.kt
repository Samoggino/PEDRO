package com.lam.pedro.presentation.serialization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreenRecords(navController: NavController) {
    val viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Activity Methods") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // List of activities
            ActivityType.entries.forEach { activityType ->
                ActivityRow(
                    activityType = activityType,
                    onInsertClick = {
                        viewModel.insertActivitySession(
                            context = context,
                            activityType = activityType
                        )
                    },
                    onGetClick = {
                        when (activityType) {
                            ActivityType.YOGA -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.RUN -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.CYCLING -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.LIFT -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.DRIVE -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.WALK -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.SIT -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.SLEEP -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.TRAIN -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                            ActivityType.LISTEN -> viewModel.getActivitySession(
                                context,
                                activityType
                            )

                        }
                    }
                )

            }

            // Navigation buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                NavigationButton(
                    text = "Vai alla schermata dei follower",
                    onClick = { navController.navigate(Screen.FollowScreen.route) }
                )
                NavigationButton(
                    text = "Vai alla schermata dei charts",
                    onClick = { navController.navigate(Screen.ChartsScreen.route) }
                )
            }
        }
    }
}

@Composable
fun ActivityRow(
    activityType: ActivityType,
    onInsertClick: () -> Unit,
    onGetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onInsertClick) {
            Text("Insert ${activityType.name}")
        }
        Button(onClick = onGetClick) {
            Text("Get ${activityType.name}")
        }
    }
}

@Composable
fun NavigationButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}
