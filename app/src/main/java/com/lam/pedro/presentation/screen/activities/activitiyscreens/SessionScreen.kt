package com.lam.pedro.presentation.screen.activities.activitiyscreens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.charts.LabelMetrics
import com.lam.pedro.presentation.charts.MetricSelector
import com.lam.pedro.presentation.charts.TimePeriod
import com.lam.pedro.presentation.charts.getAvailableMetricsForActivity
import com.lam.pedro.presentation.component.ActivityMetricCarousel
import com.lam.pedro.presentation.component.ActivityScreenHeader
import com.lam.pedro.presentation.component.DatePickerModal
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.navigation.Screen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    var sessionList by viewModel.sessionsList

    // Stato per il periodo del grafico
    var selectedPeriod by remember { mutableStateOf(TimePeriod.WEEKLY) }

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
            if (permissionsGranted && uiState !is ActivitySessionViewModel.UiState.Loading) {
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

        Column {
            ActivityScreenHeader(
                titleId,
                viewModel.activityEnum.color,
                viewModel.activityEnum.image
            )

            if (uiState is ActivitySessionViewModel.UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize() // La Box occupa tutto lo spazio disponibile
                        .wrapContentSize(Alignment.Center),
                    content = { CircularProgressIndicator(color = viewModel.activityEnum.color) }
                )
            } else {
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
                            // Passa direttamente selectedPeriod e onPeriodSelected come parametro
                            SessionScreenBody(
                                selectedPeriod = selectedPeriod,
                                onPeriodSelected = { selectedPeriod = it },
                                viewModel = viewModel,
                                sessionList = sessionList
                            )
                        }
                    }
                }

            }
        }

    }
}


@Composable
private fun SessionScreenBody(
    selectedPeriod: TimePeriod,
    viewModel: ActivitySessionViewModel,
    sessionList: List<GenericActivity>,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(30.dp))

        // Usando il nuovo componente StatisticsHeader
        MetricSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            activityColor = viewModel.activityEnum.color
        )

        Spacer(modifier = Modifier.height(16.dp))

        val availableMetrics = getAvailableMetricsForActivity(viewModel.activityEnum)

        // Usando il nuovo componente ActivityStatistics
        ActivityCarouselLauncher(
            sessionList = sessionList,
            selectedPeriod = selectedPeriod,
            availableMetrics = availableMetrics,
            activityEnum = viewModel.activityEnum
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


@Composable
fun ActivityCarouselLauncher(
    sessionList: List<GenericActivity>,
    selectedPeriod: TimePeriod,
    availableMetrics: List<LabelMetrics>,
    activityEnum: ActivityEnum
) {
    // Aggiungi la chiave per forzare il ricaricamento del grafico ogni volta che cambia il periodo
    key(selectedPeriod) {
        ActivityMetricCarousel(
            activities = sessionList,
            availableMetrics = availableMetrics,
            timePeriod = selectedPeriod,
            activityEnum = activityEnum
        )
    }
}