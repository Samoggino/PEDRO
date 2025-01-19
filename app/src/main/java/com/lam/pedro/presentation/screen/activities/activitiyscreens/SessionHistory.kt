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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.DatePickerModal
import com.lam.pedro.presentation.screen.community.user.SessionHistoryGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SessionHistory(
    viewModel: ActivitySessionViewModel,
    coroutineScope: CoroutineScope
) {
    val sessionList by viewModel.sessionListStateFlow.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Activity History", // Usa il testo direttamente
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            FilterComponent(
                viewModel = viewModel,
                coroutineScope = coroutineScope,
                onReset = {
                    coroutineScope.launch {
                        viewModel.resetSelectedDate()
                    }
                },
                onDateSelected = { timestamp ->
                    if (timestamp != null) {
                        viewModel.updateSelectedDate(
                            Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )

                        Log.d("SessionHistory", "Timestamp: $timestamp SelectedDate: $selectedDate")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

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


                        Log.d("SessionHistory", "SelectedDate: $selectedDate")
                        val displayText = selectedDate?.format(
                            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
                        ) ?: "All"
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }

                item {
                    if (sessionList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No sessions", // Usa il testo direttamente
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {

                        SessionHistoryGroup(
                            sessions = sessionList,
                            selectedActivityType = viewModel.activityEnum,
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
    onReset: () -> Unit,
    onDateSelected: (Long?) -> Unit
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
                onDateSelected = {
                    onDateSelected(it)
                    isDatePickerVisible = false
                },
                onDismiss = { isDatePickerVisible = false },
                accentColor = viewModel.activityEnum.color
            )
        }
    }
}


