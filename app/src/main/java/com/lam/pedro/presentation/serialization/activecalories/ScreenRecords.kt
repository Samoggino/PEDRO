package com.lam.pedro.presentation.serialization.activecalories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun MyScreenRecords(
    navController: NavController,
) {

    val viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Column(modifier = Modifier.padding(16.dp)) {

        // TRAIN
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.insertActivitySession(context, ActivityType.TRAIN)
                    }
                },
            ) {
                Text("insert ${ActivityType.TRAIN}")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitySession(context, ActivityType.TRAIN)
                    }
                },
            ) {
                Text("DAMMI IL ${ActivityType.TRAIN}, PER DIO")
            }
        }

        // CYCLING
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.insertActivitySession(context, ActivityType.CYCLING)
                    }
                },
            ) {
                Text("insert ${ActivityType.CYCLING}")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitySession(context, ActivityType.CYCLING)
                    }
                },
            ) {
                Text("DAMMI LA ${ActivityType.CYCLING}, PER DIO")
            }
        }


        // YOGA
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.insertActivitySession(context, ActivityType.YOGA)
                    }
                },
            ) {
                Text("insert ${ActivityType.YOGA}")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitySession(context, ActivityType.YOGA)
                    }
                },
            ) {
                Text("DAMMI LO YOGA, PER DIO")
            }
        }

        // RUN
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.insertActivitySession(context, ActivityType.RUN)
                    }
                },
            ) {
                Text("insert ${ActivityType.RUN}")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitySession(context, ActivityType.RUN)
                    }
                },
            ) {
                Text("DAMMI LA ${ActivityType.RUN}, PER DIO")
            }
        }


        // DRIVE
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.insertActivitySession(context, ActivityType.DRIVE)
                    }
                },
            ) {
                Text("insert ${ActivityType.DRIVE}")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitySession(context, ActivityType.DRIVE)
                    }
                },
            ) {
                Text("DAMMI LA ${ActivityType.DRIVE}, PER DIO")
            }
        }

        // LIFT
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.insertActivitySession(context, ActivityType.LIFT)
                    }
                },
            ) {
                Text("insert ${ActivityType.LIFT}")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitySession(context, ActivityType.LIFT)
                    }
                },
            ) {
                Text("DAMMI IL ${ActivityType.LIFT}, PER DIO")
            }
        }


        Column(
            // mettilo in mezzo
            modifier = Modifier.padding(16.dp)
        ) {
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
}
