package com.lam.pedro.presentation.screen.activities.staticactivities.liftscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.units.Mass
import androidx.navigation.NavHostController
import com.lam.pedro.R
import com.lam.pedro.data.WeightData
import com.lam.pedro.presentation.component.ActivityScreenHeader
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.SessionHistoryRow
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.dynamicactivities.runscreen.RunSessionViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    uiState: ActivitySessionViewModel.UiState,
    sessionsList: List<ExerciseSessionRecord>,
    onInsertClick: (Double) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    onStartRecording: () -> Unit = {},
    navController: NavHostController,
    titleId: Int,
    color: Color,
    image: Int,
    viewModel: LiftSessionViewModel
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val sessionList by viewModel.sessionsList

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is ActivitySessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [InputReadingsScreenViewModel.UiState] provides details of whether the last action
        // was a success or resulted in an error. Where an error occurred, for example in reading
        // and writing to Health Connect, the user is notified, and where the error is one that can
        // be recovered from, an attempt to do so is made.
        if (uiState is ActivitySessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    //var weightInput by remember { mutableStateOf("") }

    // Check if the input value is a valid weight
    fun hasValidDoubleInRange(weight: String): Boolean {
        val tempVal = weight.toDoubleOrNull()
        return if (tempVal == null) {
            false
        } else tempVal <= 1000
    }

    Scaffold(
        floatingActionButton = {
            if (permissionsGranted) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.NewActivityScreen.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add Activity") },
                    text =
                    { Text("Start Session") },
                    shape = RoundedCornerShape(26.dp),
                    containerColor = color, // Colore del bottone
                    contentColor = Color.White // Colore del contenuto (testo e icona)
                )
            }
        }
    ) { paddingValues ->
        if (uiState != ActivitySessionViewModel.UiState.Uninitialized) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    ActivityScreenHeader(titleId, color, image)
                }
                if (!permissionsGranted) {
                    item {
                        PermissionRequired(
                            color = color,
                            permissions = permissions,
                            onPermissionLaunch = onPermissionsLaunch
                        )
                    }
                } else {
                    item {

                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(26.dp))
                                .height(180.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            // TODO: graph
                        }
                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = stringResource(R.string.activity_history),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier
                                .clip(RoundedCornerShape(26.dp))
                                .height(350.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            items(sessionList) { session ->
                                SessionHistoryRow(color, image, session, viewModel)
                                HorizontalDivider(
                                    thickness = 1.dp, // Spessore della linea
                                    color = Color(0xFF606060) // Colore della linea
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }

                        /*
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = {
                                weightInput = it
                            },
                            label = {
                                Text(stringResource(id = R.string.weight_input))
                            },
                            isError = !hasValidDoubleInRange(weightInput),
                            keyboardActions = KeyboardActions { !hasValidDoubleInRange(weightInput) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.clip(RoundedCornerShape(26.dp))
                        )
                        if (!hasValidDoubleInRange(weightInput)) {
                            Text(
                                text = stringResource(id = R.string.valid_weight_error_message),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }


                        Button(
                            enabled = hasValidDoubleInRange(weightInput),
                            onClick = {
                                onInsertClick(weightInput.toDouble())
                                // clear TextField when new weight is entered
                                weightInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7771C9), // Colore di sfondo
                                contentColor = Color.White         // Colore del testo
                            ),
                            modifier = Modifier.fillMaxHeight()

                        ) {
                            Text(text = stringResource(id = R.string.add_readings_button))
                        }

                        Text(
                            text = stringResource(id = R.string.previous_readings),
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    items(readingsList) { reading ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            /*
                            Image(
                                modifier = Modifier
                                    .padding(2.dp, 2.dp)
                                    .height(16.dp)
                                    .width(16.dp),
                                painter = rememberDrawablePainter(drawable = reading.sourceAppInfo?.icon),
                                contentDescription = "App Icon"
                            )
                             */
                            Text(
                                text = "%.1f ${stringResource(id = R.string.kilograms)}"
                                    .format(reading.weight.inKilograms)
                            )
                            Text(text = formatter.format(reading.time))
                            IconButton(
                                onClick = { onDeleteClick(reading.id) },
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    stringResource(R.string.delete_button_readings)
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = stringResource(id = R.string.weekly_avg), fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                        if (weeklyAvg == null) {
                            Text(text = "0.0")
                        } else {
                            Text(
                                text = "%.1f ${stringResource(id = R.string.kilograms)}"
                                    .format(weeklyAvg.inKilograms)
                            )
                        }
                    }

                         */
                    }
                }
            }
        }
    }



