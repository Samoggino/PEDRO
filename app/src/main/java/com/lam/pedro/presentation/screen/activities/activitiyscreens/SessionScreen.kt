package com.lam.pedro.presentation.screen.activities.activitiyscreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.charts.LabelMetrics
import com.lam.pedro.presentation.charts.ViewModelCharts
import com.lam.pedro.presentation.charts.getAvailableMetricsForActivity
import com.lam.pedro.presentation.component.ActivityScreenHeader
import com.lam.pedro.presentation.component.DisplayGraph
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.SessionHistoryRow
import com.lam.pedro.presentation.navigation.Screen
import java.util.UUID

@Composable
fun SessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    uiState: ActivitySessionViewModel.UiState,
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    onNavigate: (String) -> Unit,
    titleId: Int,
    viewModel: ActivitySessionViewModel
) {
    Log.d("TIPO DELLO SCREEN", "---- TIPO DELLO SCREEN: ${viewModel.activityEnum.activityType}")
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val sessionList by viewModel.sessionsList

    // Stato per il periodo del grafico
    var selectedPeriod by remember { mutableStateOf(ViewModelCharts.TimePeriod.WEEKLY) }

    LaunchedEffect(uiState) {
        if (uiState is ActivitySessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }
        if (uiState is ActivitySessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    Scaffold(
        floatingActionButton = {
            if (permissionsGranted) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigate(Screen.NewActivityScreen.route) },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add Activity") },
                    text = { Text("Start Session") },
                    shape = RoundedCornerShape(26.dp),
                    containerColor = viewModel.activityEnum.color,
                    contentColor = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = paddingValues.calculateBottomPadding()),
            horizontalAlignment = if (!permissionsGranted) {
                Alignment.CenterHorizontally
            } else {
                Alignment.Start
            }
        ) {
            item {
                ActivityScreenHeader(
                    titleId,
                    viewModel.activityEnum.color,
                    viewModel.activityEnum.image
                )
            }

            if (!permissionsGranted) {
                item { Spacer(modifier = Modifier.height(30.dp)) }
                item {
                    PermissionRequired(
                        color = viewModel.activityEnum.color,
                        permissions = permissions,
                        onPermissionLaunch = onPermissionsLaunch
                    )
                }

            } else {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(30.dp))

                        // Usando il nuovo componente StatisticsHeader
                        StatisticsHeader(
                            selectedPeriod = selectedPeriod,
                            onPeriodSelected = { selectedPeriod = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val availableMetrics =
                            getAvailableMetricsForActivity(viewModel.activityEnum)

                        // Usando il nuovo componente ActivityStatistics
                        ActivityStatistics(
                            sessionList = sessionList,
                            selectedPeriod = selectedPeriod,
                            availableMetrics = availableMetrics
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        // Usando il nuovo componente SessionHistory
                        SessionHistory(
                            sessionList = sessionList,
                            activityEnum = viewModel.activityEnum,
                            viewModel = viewModel
                        )

                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsHeader(
    selectedPeriod: ViewModelCharts.TimePeriod,
    onPeriodSelected: (ViewModelCharts.TimePeriod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // Mostra il periodo selezionato come TextField
        TextField(
            readOnly = true,
            value = selectedPeriod.name,
            onValueChange = { },
            label = { Text("Period") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Menu a discesa con le opzioni del periodo
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ViewModelCharts.TimePeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.name) },
                    onClick = {
                        onPeriodSelected(period) // Seleziona il periodo
                        expanded = false // Chiudi il menu dopo la selezione
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}


@Composable
fun ActivityStatistics(
    sessionList: List<GenericActivity>,
    selectedPeriod: ViewModelCharts.TimePeriod,
    availableMetrics: List<LabelMetrics>
) {
    // Aggiungi la chiave per forzare il ricaricamento del grafico ogni volta che cambia il periodo
    key(selectedPeriod) {
        DisplayGraph(
            items = sessionList,
            availableMetrics = availableMetrics,
            timePeriod = selectedPeriod
        )
    }
}


@Composable
fun SessionHistory(
    sessionList: List<GenericActivity>, activityEnum: ActivityEnum,
    viewModel: ActivitySessionViewModel
) {
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .height(350.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        if (sessionList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.empty_history),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(sessionList) { session ->
                SessionHistoryRow(
                    color = activityEnum.color,
                    image = activityEnum.image,
                    session = session,
                    viewModel = viewModel
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF606060)
                )
            }
        }
    }
}

