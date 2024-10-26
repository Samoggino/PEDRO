package com.lam.pedro.presentation.screen.activities.staticactivities.weightscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.units.Mass
import androidx.navigation.NavHostController
import com.lam.pedro.data.WeightData
import com.lam.pedro.R
import com.lam.pedro.presentation.component.PermissionRequired
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    readingsList: List<WeightData>,
    uiState: InputReadingsViewModel.UiState,
    onInsertClick: (Double) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    weeklyAvg: Mass?,
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    navController: NavHostController,
    titleId: Int,
    color: Color
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is InputReadingsViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [InputReadingsScreenViewModel.UiState] provides details of whether the last action
        // was a success or resulted in an error. Where an error occurred, for example in reading
        // and writing to Health Connect, the user is notified, and where the error is one that can
        // be recovered from, an attempt to do so is made.
        if (uiState is InputReadingsViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    var weightInput by remember { mutableStateOf("") }

    // Check if the input value is a valid weight
    fun hasValidDoubleInRange(weight: String): Boolean {
        val tempVal = weight.toDoubleOrNull()
        return if (tempVal == null) {
            false
        } else tempVal <= 1000
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(titleId),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState != InputReadingsViewModel.UiState.Uninitialized) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!permissionsGranted) {
                    item {
                        PermissionRequired(color) { onPermissionsLaunch(permissions) }
                    }
                } else {
                    item {
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
                }
            }
        }
    }
}


