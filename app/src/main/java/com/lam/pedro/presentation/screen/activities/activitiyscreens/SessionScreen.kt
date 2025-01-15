package com.lam.pedro.presentation.screen.activities.activitiyscreens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.data.CarouselItem
import com.lam.pedro.presentation.component.ActivityScreenHeader
import com.lam.pedro.presentation.component.DatePickerModal
import com.lam.pedro.presentation.component.DisplayGraph
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.SessionHistoryRow
import com.lam.pedro.presentation.navigation.Screen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Composable
fun SessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    uiState: ActivitySessionViewModel.UiState,
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    navController: NavController,
    titleId: Int,
    viewModel: ActivitySessionViewModel
) {
    Log.d("TIPO DELLO SCREEN", "---- TIPO DELLO SCREEN: ${viewModel.activityEnum.activityType}")
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    var sessionList by viewModel.sessionsList
    val coroutineScope = rememberCoroutineScope()

    var isDatePickerVisible by remember { mutableStateOf(false) } // Stato per il DatePickerModal
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) } // Stato per la data selezionata

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
            item { ActivityScreenHeader(titleId, viewModel.activityEnum.color, viewModel.activityEnum.image) }

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
                    Column() {
                        Spacer(modifier = Modifier.height(30.dp))
                        //-------------------------------------------------
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        /*
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(26.dp))
                                .height(180.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            // TODO: graph
                        }

                         */
                        val items = remember {
                            listOf(
                                CarouselItem(0, "Chart one"),
                                CarouselItem(1, "Chart two"),
                                CarouselItem(2, "Chart three"),
                                CarouselItem(3, "Chart four"),
                                CarouselItem(4, "Chart five"),
                            )
                        }

                        DisplayGraph(items)

                        Spacer(modifier = Modifier.height(30.dp))
                        //-------------------------------------------------

                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.activity_history),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Box {


                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp), // Spazio tra i bottoni
                                        verticalAlignment = Alignment.CenterVertically // Allineamento verticale
                                    ) {
                                        // Bottone per filtrare
                                        Button(
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = viewModel.activityEnum.color, // Colore di sfondo del bottone
                                                contentColor = Color.White // Colore del contenuto (icona o testo)
                                            ),
                                            onClick = {
                                                isDatePickerVisible = true
                                            } // Mostra il DatePickerModal
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday, // Usa un'icona predefinita di calendario
                                                contentDescription = stringResource(R.string.select_date)
                                            )
                                        }

                                        // Bottone per resettare
                                        Button(
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = viewModel.activityEnum.color, // Colore di sfondo del bottone
                                                contentColor = Color.White // Colore del contenuto (icona o testo)
                                            ),
                                            onClick = {
                                                coroutineScope.launch {
                                                    viewModel.fetchSessions() // Avvia la coroutine per eseguire l'operazione
                                                    selectedDate = null // Resetta la data selezionata
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh, // Icona di refresh
                                                contentDescription = stringResource(R.string.reset_sessions)
                                            )
                                        }
                                    }

                                    // Mostra il DatePickerModal se visibile
                                    if (isDatePickerVisible) {
                                        DatePickerModal(
                                            onDateSelected = { timestamp ->
                                                if (timestamp != null) {
                                                    val localDate = Instant.ofEpochMilli(timestamp)
                                                        .atZone(ZoneId.systemDefault())
                                                        .toLocalDate()
                                                    selectedDate = localDate
                                                    coroutineScope.launch {
                                                        viewModel.fetchSessions() // Avvia la coroutine per eseguire l'operazione
                                                        sessionList = viewModel.filterSessionsByDay(sessionList, localDate)
                                                    }
                                                }
                                                isDatePickerVisible = false
                                            },
                                            onDismiss = {
                                                isDatePickerVisible = false
                                            },
                                            accentColor = viewModel.activityEnum.color
                                        )
                                    }
                                }

                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(26.dp))
                                    .height(350.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp), // Padding verticale per separare dal bordo
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Se selectedDate è null, mostra "All", altrimenti mostra la data formattata
                                        val displayText = selectedDate?.format(
                                            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH) // Imposta la lingua in inglese
                                        ) ?: "All" // Testo di default quando la data non è selezionata

                                        Text(
                                            text = displayText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White
                                        )
                                    }

                                }
                                Log.d("TEST SESSION LIST", sessionList.toString())
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
                                        SessionHistoryRow(viewModel.activityEnum.color, viewModel.activityEnum.image, session, viewModel)
                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            color = Color(0xFF606060)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }

    }
}
