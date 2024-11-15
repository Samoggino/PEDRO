package com.lam.pedro.presentation.serialization.activecalories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun MyScreenRecords(
    navController: NavController,
) {

    val viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
    val coroutineScope = rememberCoroutineScope()


    // Caricamento dei dati all'interno di LaunchedEffect
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.actionOne()
                }
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("TrainData")
        }
        Button(
            onClick = {
                viewModel.actionTwo()
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("CyclingData")
        }
        Button(
            onClick = {
                viewModel.actionThree()
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("YogaData")
        }
        Button(
            onClick = {
                viewModel.actionFour()
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("RunData")
        }

        Button(
            onClick = {
                navController.navigate(Screen.ExerciseSessionData.route)
            }
        ) {
            Text(
                "Vai alla schermata di esercizio"
            )
        }

        Button(
            onClick = {
                navController.navigate(Screen.SleepSessionData.route)
            }
        ) {
            Text(
                "Vai alla schermata di sonno"
            )
        }
        Button(
            onClick = {
                navController.navigate(Screen.SleepSessionData.route)
            }
        ) {
            Text(
                "Vai alla schermata di sonno"
            )
        }

        Button(
            onClick = {
                navController.navigate(Screen.FollowScreen.route)
            }
        ) {
            Text(
                "Vai alla schermata dei follower"
            )
        }


    }
}
