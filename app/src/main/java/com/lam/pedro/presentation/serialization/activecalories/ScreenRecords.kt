package com.lam.pedro.presentation.serialization.activecalories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.units.Energy
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.presentation.navigation.Screen
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@Composable
fun ButtonList(
    navController: NavController,
) {

    val activeCaloriesBurned by remember { mutableStateOf(emptyList<ActiveCaloriesBurnedRecord>()) }
    val viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val end2 = ZonedDateTime.now()
    val end1 = end2.minusDays(1)
    val start1 = end1.minusHours(5)

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                coroutineScope.launch {

                    val response = viewModel.actionOne(
                        navController = navController,
                        record = ActiveCaloriesBurnedRecord(
                            startTime = start1.toInstant(),
                            startZoneOffset = start1.offset,
                            endTime = end1.toInstant(),
                            endZoneOffset = end1.offset,
                            energy = Energy.kilocalories(100.0),
                        ),
                        context = context
                    )
                }

            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("Bottone 1")
        }
        Button(
            onClick = {
                viewModel.actionTwo()
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("Bottone 2")
        }
        Button(
            onClick = {
                viewModel.actionThree(context)
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("Bottone 3")
        }
        Button(
            onClick = {
                viewModel.actionFour(context)
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("Bottone 4")
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


    }
}


// Esempio di utilizzo del composable in una schermata
@Composable
fun MyScreenRecords(navController: NavController) {
    ButtonList(navController)
}
