package com.lam.pedro.presentation.screen.activities.activitiyscreens

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.presentation.component.DatePickerModal
import com.lam.pedro.presentation.component.SessionHistoryRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SessionHistory(
    viewModel: ActivitySessionViewModel,
    coroutineScope: CoroutineScope
) {
    // Osserva la lista delle sessioni direttamente dal ViewModel
    val sessionList by viewModel.sessionListStateFlow.collectAsState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

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

            // Passa il callback per il reset e l'aggiornamento della data
            FilterComponent(
                viewModel = viewModel,
                coroutineScope = coroutineScope,
                selectedDate = selectedDate,
                onReset = {
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.fetchSessions() // Ripristina le sessioni
                        selectedDate = null // Resetta la data selezionata
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Usa la lista di sessioni osservata per aggiornare dinamicamente la UI
        key(sessionList) {
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
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Visualizza la data selezionata o "All"
                        val displayText = selectedDate?.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy",
                                Locale.ENGLISH
                            )
                        )
                            ?: "All"
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }

                if (sessionList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
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
                            color = viewModel.activityEnum.color,
                            image = viewModel.activityEnum.image,
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
    }
}


@Composable
private fun FilterComponent(
    viewModel: ActivitySessionViewModel,
    coroutineScope: CoroutineScope,
    selectedDate: LocalDate?,
    onReset: () -> Unit
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }

    Box {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bottone per il DatePicker
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = viewModel.activityEnum.color,
                    contentColor = Color.White
                ),
                onClick = { isDatePickerVisible = true }
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.select_date)
                )
            }

            // Bottone per resettare
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = viewModel.activityEnum.color,
                    contentColor = Color.White
                ),
                onClick = {
                    coroutineScope.launch {
                        onReset() // Chiama la funzione di reset
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.reset_sessions)
                )
            }
        }

        // Mostra il DatePicker se visibile
        if (isDatePickerVisible) {
            DatePickerModal(
                onDateSelected = { timestamp ->
                    if (timestamp != null) {
                        val localDate = Instant.ofEpochMilli(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        // Chiamata alla funzione del ViewModel per aggiornare la lista
                        viewModel.onDateSelected(localDate) // Funzione del ViewModel
                    }
                    isDatePickerVisible = false
                },
                onDismiss = { isDatePickerVisible = false },
                accentColor = viewModel.activityEnum.color
            )
        }
    }
}
