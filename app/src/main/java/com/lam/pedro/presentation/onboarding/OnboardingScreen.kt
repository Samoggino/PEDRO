package com.lam.pedro.presentation.onboarding

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
) {
    Log.i("OnboardingScreen", "OnboardingScreen reloaded")

    val viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModelFactory())

    val pages = listOf(
        OnboardingModel.FirstPage,
        OnboardingModel.SecondPage,
        OnboardingModel.ThirdPage,
        OnboardingModel.FourthPage
    )

    var currentPage by remember { mutableStateOf(0) }

    val buttonState = remember {
        derivedStateOf {
            when (currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Next")
                3 -> listOf("Back", "Vamos")
                else -> listOf("", "")
            }
        }
    }

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (buttonState.value[0].isNotEmpty()) {
                    Log.d("OnboardingScreen", "Button text: ${buttonState.value[0]}")

                    NextButtonUI(
                        text = buttonState.value[0],
                        backgroundColor = Color.Transparent,
                        textColor = Color.White
                    ) {
                        Log.d("OnboardingScreen", "Button clicked")

                        // Scorri alla pagina precedente
                        if (buttonState.value[0] == "Back" && currentPage > 0) {
                            currentPage -= 1
                        } else if (currentPage < pages.size - 1) {
                            // Scorri alla pagina successiva
                            currentPage += 1
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                IndicatorUI(pageSize = pages.size, currentPage = currentPage)
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                NextButtonUI(
                    text = buttonState.value[1],
                    backgroundColor = if (currentPage == 2 && !viewModel.areProfileFieldsValid()) {
                        Color.Gray // Colore grigio per stato non valido
                    } else {
                        MaterialTheme.colorScheme.primary // Colore standard
                    },
                    textColor = if (currentPage == 2 && !viewModel.areProfileFieldsValid()) {
                        Color.LightGray // Testo disabilitato
                    } else {
                        MaterialTheme.colorScheme.onPrimary // Testo standard
                    },
                    isEnabled = !(currentPage == 2 && !viewModel.areProfileFieldsValid()) // Disabilita il bottone se i dati non sono validi
                ) {
                    if (currentPage == pages.size - 1) {
                        Log.d("OnboardingScreen", "Sending data...")
                        viewModel.areProfileFieldsValid()
                        onFinished() // Conclude l'onboarding
                    } else if (currentPage == 2) {
                        // Valida i dati della terza pagina
                        if (viewModel.areProfileFieldsValid()) {
                            Log.d("OnboardingScreen", "Profile fields are valid!")
                            currentPage += 1
                        } else {
                            Log.d("OnboardingScreen", "Profile fields are invalid!")
                        }
                    } else {
                        currentPage += 1
                    }
                }
            }
        }
    }, content = {
        Column(Modifier.padding(it)) {
            // Usa AnimatedContent per animare la transizione tra le pagine
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            ) { targetPage ->
                when (targetPage) {
                    0 -> OnboardingGraphUI(onboardingModel = pages[0])
                    1 -> OnboardingGraphUI(onboardingModel = pages[1])
                    2 -> ThirdPageContent(
                        firstName = viewModel.firstName,
                        lastName = viewModel.lastName,
                        age = viewModel.age,
                        sex = viewModel.sex,
                        weight = viewModel.weight,
                        height = viewModel.height,
                        nationality = viewModel.nationality,
                    )

                    3 -> OnboardingGraphUI(onboardingModel = pages[3])
                }
            }
        }
    })
}


@Composable
fun ThirdPageContent(
    firstName: MutableState<String>,
    lastName: MutableState<String>,
    age: MutableState<String>,
    sex: MutableState<String>,
    weight: MutableState<String>,
    height: MutableState<String>,
    nationality: MutableState<String>,
) {
    val showErrors by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tell me more about you gringo...",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "I will use info such as age, height, etc for a more accurate calorie analysis",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // First Name
        TextField(
            value = firstName.value,
            onValueChange = { if (it.length <= 15) firstName.value = it },
            label = { Text("First Name") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            isError = showErrors && firstName.value.isBlank()
        )
        if (showErrors && firstName.value.isBlank()) {
            Text("First name is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Last Name
        TextField(
            value = lastName.value,
            onValueChange = { if (it.length <= 15) lastName.value = it },
            label = { Text("Last Name") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            isError = showErrors && lastName.value.isBlank()
        )
        if (showErrors && lastName.value.isBlank()) {
            Text("Last name is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Age
        TextField(
            value = age.value,
            onValueChange = { age.value = it.filter { char -> char.isDigit() } },
            label = { Text("Age") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && age.value.isBlank()
        )
        if (showErrors && age.value.isBlank()) {
            Text("Age is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Sex
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { sex.value = "male" }
            ) {
                RadioButton(
                    selected = sex.value == "male",
                    onClick = { sex.value = "male" }
                )
                Text(text = "Male")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { sex.value = "female" }
            ) {
                RadioButton(
                    selected = sex.value == "female",
                    onClick = { sex.value = "female" }
                )
                Text(text = "Female")
            }
        }
        if (showErrors && sex.value.isBlank()) {
            Text("Sex is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Weight
        TextField(
            value = weight.value,
            onValueChange = { weight.value = it },
            label = { Text("Weight (kg)") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && weight.value.isBlank()
        )
        if (showErrors && weight.value.isBlank()) {
            Text("Weight is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Height
        TextField(
            value = height.value,
            onValueChange = { height.value = it },
            label = { Text("Height (cm)") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && height.value.isBlank()
        )
        if (showErrors && height.value.isBlank()) {
            Text("Height is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Nationality
        NationalitySelector(nationality = nationality)
        if (showErrors && nationality.value.isBlank()) {
            Text("Nationality is required", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun NationalitySelector(nationality: MutableState<String>) {
    val countries = listOf(
        "Italy", "United States", "United Kingdom", "France", "Germany",
        "Spain", "Canada", "Australia", "India", "China", "Japan", "Brazil"
    )

    val countryFlags = mapOf(
        "Italy" to "\uD83C\uDDEE\uD83C\uDDF9",
        "United States" to "\uD83C\uDDFA\uD83C\uDDF8",
        "United Kingdom" to "\uD83C\uDDEC\uD83C\uDDE7",
        "France" to "\uD83C\uDDEB\uD83C\uDDF7",
        "Germany" to "\uD83C\uDDE9\uD83C\uDDEA",
        "Spain" to "\uD83C\uDDEA\uD83C\uDDF8",
        "Canada" to "\uD83C\uDDE8\uD83C\uDDE6",
        "Australia" to "\uD83C\uDDE6\uD83C\uDDFA",
        "India" to "\uD83C\uDDEE\uD83C\uDDF3",
        "China" to "\uD83C\uDDE8\uD83C\uDDF3",
        "Japan" to "\uD83C\uDDEF\uD83C\uDDF5",
        "Brazil" to "\uD83C\uDDE7\uD83C\uDDF7"
    )

    var showDialog by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { showDialog = true }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (nationality.value.isNotEmpty()) {
                        val flag = countryFlags[nationality.value] ?: ""
                        "$flag ${nationality.value}"
                    } else "Select Nationality",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (nationality.value.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open selector",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Select your nationality") },
                text = {
                    LazyColumn {
                        items(countries) { country ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        nationality.value = country
                                        showDialog = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val flag = countryFlags[country] ?: ""
                                Text(
                                    text = "$flag $country",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}