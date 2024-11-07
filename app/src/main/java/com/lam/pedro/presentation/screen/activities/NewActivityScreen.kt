package com.lam.pedro.presentation.screen.activities

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.DisplayLottieAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewActivityScreen(
    navController: NavController,
    titleId: Int,
    color: Color,
    viewModel: ActivitySessionViewModel,
    activityType: Int
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(titleId) + " - New activity",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        BackButton(navController)
                    }
                }


            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
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

            Spacer(modifier = Modifier.height(60.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Pulsante Pausa/Play
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .size(150.dp)
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
                            modifier = Modifier.size(75.dp)
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
                            .size(150.dp)
                            .background(Color(0xFFF44336))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.stop_icon),
                            contentDescription = "Stop",
                            modifier = Modifier.size(75.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            var title by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            var isTitleEmpty by remember { mutableStateOf(false) } // Variabile per controllare se il titolo è vuoto

// Mostra il dialogo di conferma
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Confirm", color = color) },
                    text = {
                        Column {
                            // Messaggio di conferma in base all'azione
                            Text(
                                if (isStopAction) "Want to stop the activity? (you can change these while stopping)" else if (visible) "Want to pause?" else "Want to start?"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Campo di input per il titolo
                            OutlinedTextField(
                                value = title,
                                onValueChange = {
                                    title = it
                                    isTitleEmpty = title.isBlank() // Controlla se il titolo è vuoto
                                },
                                label = { Text("Titolo") },
                                isError = isTitleEmpty,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(26.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = color,
                                    cursorColor = color,
                                    focusedLabelColor = color,
                                )
                            )

                            if (isTitleEmpty) {
                                Text(
                                    text = "Title is required",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo di input per le note
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Note (optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(26.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = color,
                                    cursorColor = color,
                                    focusedLabelColor = color,
                                )
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (title.isNotBlank()) { // Verifica che il titolo non sia vuoto
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

                                            // Salva i dati usando i valori inseriti dall'utente
                                            viewModel.saveExerciseTest(
                                                startTime,
                                                endTime,
                                                activityType,
                                                title,
                                                notes
                                            )
                                            viewModel.fetchExerciseSessions(activityType)

                                            elapsedTime = 0

                                            navController.popBackStack()
                                        } else {
                                            visible = !visible // Alterna il valore di visible

                                            if (visible) {
                                                isPaused = false // Avvia il timer
                                                timerRunning = true
                                            }
                                        }
                                        showDialog = false // Chiude il dialogo
                                    }
                                } else {
                                    isTitleEmpty = true // Imposta l'errore se il titolo è vuoto
                                }

                            }
                        ) {
                            Text(text = "Yes", color = color)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = "Dismiss", color = color)
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

                Column() {
                    Text(
                        String.format("%02d:%02d:%02d", minutes, seconds, centiseconds),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally) // Centro orizzontalmente
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    DisplayLottieAnimation("https://lottie.host/484dc375-3dc0-4d9a-a788-455943b5cc61/wmTnJFRiES.lottie")
                }

            }

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