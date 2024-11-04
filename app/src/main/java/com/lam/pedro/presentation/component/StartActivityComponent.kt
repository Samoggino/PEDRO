package com.lam.pedro.presentation.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime


@Composable
fun StartActivityComponent(color: Color, image: Int, viewModel: ActivitySessionViewModel, navController: NavController) {
    // Osserva sessionList dal ViewModel
    val sessionList by viewModel.sessionsList
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        val density = LocalDensity.current
        var showDialog by remember { mutableStateOf(false) }
        var isStopAction by remember { mutableStateOf(false) }
        var visible by remember { mutableStateOf(false) }
        var isPaused by remember { mutableStateOf(true) }

        val coroutineScope = rememberCoroutineScope()

        // Variabili per il timer
        var timerRunning by remember { mutableStateOf(false) }
        var elapsedTime by remember { mutableStateOf(0) } // tempo in millisecondi

        var startTime: ZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }
        var endTime: ZonedDateTime

        // Lista dei risultati dei timer
        val timerResults = remember { mutableStateListOf<String>() }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Pulsante Pausa/Play
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .size(70.dp)
                    .background(color)
            ) {
                IconButton(
                    onClick = {
                        if (visible) {
                            isPaused = !isPaused
                        } else {
                            isStopAction = false
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxSize() // Assicura che l'IconButton riempia il Box
                ) {
                    Image(
                        painter = painterResource(id = if (!isPaused) R.drawable.pause_icon else R.drawable.play_icon),
                        contentDescription = if (visible) "Pause" else "Play",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Pulsante Stop
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally { with(density) { -40.dp.roundToPx() } } + fadeIn(),
                exit = slideOutHorizontally { with(density) { -40.dp.roundToPx() } } + fadeOut()
            ) {

                IconButton(
                    onClick = {
                        isStopAction = true
                        showDialog = true
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .size(70.dp)
                        .background(Color(0xFFF44336))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.stop_icon),
                        contentDescription = "Stop",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Mostra il dialogo di conferma
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Conferma") },
                text = {
                    Text(
                        if (isStopAction) "Vuoi fermare l'attività?" else if (visible) "Vuoi mettere in pausa?" else "Vuoi avviare l'attività?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                if (isStopAction) {
                                    timerRunning = false // Ferma il timer
                                    visible = false // Nascondi il pulsante di pausa
                                    isPaused = true // Imposta il timer in pausa
                                    // Aggiungi il tempo finale alla lista dei risultati
                                    val minutes = (elapsedTime / 60000) % 60
                                    val seconds = (elapsedTime / 1000) % 60
                                    val centiseconds = (elapsedTime % 1000) / 10
                                    timerResults.add(
                                        String.format(
                                            "%02d:%02d:%02d",
                                            minutes,
                                            seconds,
                                            centiseconds
                                        )
                                    )
                                    Log.d(TAG, "------------Timer results: $timerResults")


                                    endTime = ZonedDateTime.now()
                                    viewModel.saveExerciseTest(startTime, endTime)
                                    viewModel.readExerciseSessions()



                                    elapsedTime = 0 // Resetta il tempo
                                } else {
                                    // Alterna il valore di visible
                                    visible = !visible

                                    if (visible) {
                                        isPaused = false // Avvia il timer
                                        timerRunning = true
                                    }
                                }
                                showDialog = false // Chiude il dialogo
                            }
                        }) {
                        Text("Sì")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Annulla")
                    }
                }
            )
        }

        // Timer
        if (timerRunning && !isPaused) {
            LaunchedEffect(Unit) {
                //FIXME ad ogni resume resetta il tempo di inizio
                startTime = ZonedDateTime.now()
                while (timerRunning) {
                    delay(10) // Aggiorna ogni 10 millisecondi
                    elapsedTime += 10 // Incrementa il tempo
                }
            }
        }

        // Mostra il timer con animazione AnimatedVisibility
        AnimatedVisibility(visible = timerRunning || !isPaused) {
            // Calcolo del tempo
            val minutes = (elapsedTime / 60000) % 60
            val seconds = (elapsedTime / 1000) % 60
            val centiseconds = (elapsedTime % 1000) / 10

            Text(
                String.format("%02d:%02d:%02d", minutes, seconds, centiseconds),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally) // Centro orizzontalmente
            )
        }

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
            .height(600.dp)
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
}

/*
@Composable
fun StartActivityComponent(
    color: Color,
    viewModel: RunSessionViewModel // Passa il ViewModel
) {
    val sessionState by viewModel.sessionState.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        val density = LocalDensity.current
        var showDialog by remember { mutableStateOf(false) }
        var isStopAction by remember { mutableStateOf(false) }

        // Controlla lo stato del timer e visualizza i pulsanti di conseguenza
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Pulsante Pausa/Play
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .size(70.dp)
                    .background(color)
            ) {
                IconButton(
                    onClick = {
                        when (sessionState) {
                            SessionState.IDLE -> {
                                isStopAction = false
                                showDialog = true
                            }

                            SessionState.RUNNING -> viewModel.pauseSession()
                            SessionState.PAUSED -> viewModel.resumeSession()
                            else -> {}
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = if (sessionState == SessionState.RUNNING) R.drawable.pause_icon else R.drawable.play_icon),
                        contentDescription = "Play or Pause",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Pulsante Stop
            if (sessionState != SessionState.IDLE) {
                IconButton(
                    onClick = {
                        isStopAction = true
                        showDialog = true
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .size(70.dp)
                        .background(Color(0xFFF44336))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.stop_icon),
                        contentDescription = "Stop",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Mostra il dialogo di conferma
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Conferma") },
                text = {
                    Text(
                        if (isStopAction) "Vuoi fermare l'attività?"
                        else if (sessionState == SessionState.RUNNING) "Vuoi mettere in pausa?"
                        else "Vuoi avviare l'attività?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch { // Lancia una coroutine
                                if (isStopAction) {
                                    viewModel.stopSession() // Chiama stopSession qui
                                } else {
                                    viewModel.startSession() // Avvia la sessione
                                }
                                showDialog = false // Chiude il dialogo
                            }
                        }) {
                        Text("Sì")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Annulla")
                    }
                }
            )
        }

        // Mostra il timer
        if (sessionState != SessionState.IDLE) {
            val minutes = (elapsedTime / 60000) % 60
            val seconds = (elapsedTime / 1000) % 60
            val centiseconds = (elapsedTime % 1000) / 10

            Text(
                String.format("%02d:%02d:%02d", minutes, seconds, centiseconds),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}
*/