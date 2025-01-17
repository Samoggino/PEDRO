package com.lam.pedro.presentation.screen.activities.activitiyscreens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.component.DatePickerModal
import com.lam.pedro.presentation.component.SessionHistoryRow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


/*
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
            items(
                sessionList,
            ) { session ->
                Pair(session.basicActivity.startTime, session.basicActivity.endTime)
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

 */

@Composable
fun SessionHistory(
    sessionList: List<GenericActivity>, activityEnum: ActivityEnum,
    viewModel: ActivitySessionViewModel
) {
    var sessionLocalList by remember { mutableStateOf(sessionList) }
    var isDatePickerVisible by remember { mutableStateOf(false) } // Stato per il DatePickerModal
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) } // Stato per la data selezionata
    val coroutineScope = rememberCoroutineScope()

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
                                    sessionLocalList = viewModel.filterSessionsByDay(sessionLocalList, localDate)
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
                .padding(horizontal = 16.dp)
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
            Log.d("TEST SESSION LIST", sessionLocalList.toString())
            if (sessionLocalList.isEmpty()) {
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
                items(sessionLocalList) { session ->
                    SessionHistoryRow(viewModel.activityEnum.color, viewModel.activityEnum.image, session, viewModel)
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFF606060)
                    )
                }
            }
        }
    }
}