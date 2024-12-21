package com.lam.pedro.presentation.serialization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreenRecords(
    navController: NavController,
    viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Activity Methods") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.LoginScreen.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to login"
                        )
                    }
                }

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
            ActivityEnum.entries.forEach { activityType ->
                ActivityRow(
                    activityEnum = activityType,
                    onInsertClick = {
                        viewModel.insertActivitySession(
                            activityEnum = activityType
                        )
                    },
                    onGetClick = {
                        navController.navigate(Screen.ChartsScreen.route + "/${activityType.name}")
                    }
                )

            }

            Button(
                onClick = {
                    viewModel.dumpActivitiesFromDB()
                }
            ) {
                Text("Dump Activities from DB")
            }

            // Navigation buttons
            NavButtons(navController)
        }
    }
}

@Composable
fun NavButtons(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {


        NavigationButton(
            text = "Vai alla schermata dei follower",
            onClick = { navController.navigate(Screen.FollowScreen.route) }
        )
    }
}

@Composable
fun ActivityRow(
    activityEnum: ActivityEnum,
    onInsertClick: () -> Unit,
    onGetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onInsertClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = activityEnum.color // Cambia il colore del bottone
            )
        ) {
            Text("Insert ${activityEnum.name}")
        }

        Button(
            onClick = onGetClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = activityEnum.color // Cambia il colore del bottone
            )
        ) {
            Text("Get ${activityEnum.name}")
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
