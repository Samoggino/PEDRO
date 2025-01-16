package com.lam.pedro.presentation.onboarding

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import kotlinx.coroutines.launch

/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {

    val pages = listOf(
        OnboardingModel.FirstPage, OnboardingModel.SecondPage, OnboardingModel.ThirdPage, OnboardingModel.FourthPage
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Next")
                3 -> listOf("Back", "Vamos!")
                else -> listOf("", "")
            }
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart) { if (buttonState.value[0].isNotEmpty()) {
                NextButtonUI (text = buttonState.value[0],
                    backgroundColor = Color.Transparent,
                    textColor = Color.White) {
                    scope.launch {
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                }
            }
            }
            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center) {
                IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
            }

            Box(modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd) {
                NextButtonUI (text = buttonState.value[1],
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary) {
                    scope.launch {
                        if (pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinished()
                        }
                    }
                }
            }

        }
    }, content = {
        Column(Modifier.padding(it)) {
            HorizontalPager(state = pagerState) { index ->
                OnboardingGraphUI(onboardingModel = pages[index])
            }
        }
    })
}

 */

@Composable
fun OnboardingScreen(profileViewModel: ProfileViewModel, onFinished: () -> Unit) {

    val pages = listOf(
        OnboardingModel.FirstPage,
        OnboardingModel.SecondPage,
        OnboardingModel.ThirdPage,
        OnboardingModel.FourthPage
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Next")
                3 -> listOf("Back", "Vamos!")
                else -> listOf("", "")
            }
        }
    }

    val scope = rememberCoroutineScope()

    // Stati per i campi della terza pagina
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val age = remember { mutableStateOf("") }
    val sex = remember { mutableStateOf("") }
    val weight = remember { mutableStateOf("") }
    val height = remember { mutableStateOf("") }
    val nationality = remember { mutableStateOf("") }

    // Validazione dei campi
    val isThirdPageValid = remember {
        derivedStateOf {
            firstName.value.isNotEmpty() && firstName.value.length <= 15 &&
                    lastName.value.isNotEmpty() && lastName.value.length <= 15 &&
                    age.value.toIntOrNull()?.let { it in 1..120 } == true &&
                    (sex.value == "male" || sex.value == "female") &&
                    weight.value.toDoubleOrNull() != null &&
                    height.value.toDoubleOrNull() != null &&
                    nationality.value.isNotEmpty()
        }
    }

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (buttonState.value[0].isNotEmpty()) {
                    NextButtonUI(
                        text = buttonState.value[0],
                        backgroundColor = Color.Transparent,
                        textColor = Color.White
                    ) {
                        scope.launch {
                            if (pagerState.currentPage == 2) {
                                if (!isThirdPageValid.value) {
                                    // Mostra un messaggio di errore o notifica
                                    return@launch
                                }

                                // Salva i dati nel ViewModel
                                profileViewModel.updateProfileField("firstName", firstName.value)
                                profileViewModel.updateProfileField("lastName", lastName.value)
                                profileViewModel.updateProfileField("age", age.value)
                                profileViewModel.updateProfileField("sex", sex.value)
                                profileViewModel.updateProfileField("weight", weight.value)
                                profileViewModel.updateProfileField("height", height.value)
                                profileViewModel.updateProfileField(
                                    "nationality",
                                    nationality.value
                                )


                            }

                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onFinished()
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                NextButtonUI(
                    text = buttonState.value[1],
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    scope.launch {
                        if (pagerState.currentPage == 2 && !isThirdPageValid.value) {
                            // Mostra un messaggio di errore o notifica
                        } else if (pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinished()
                        }
                    }
                }
            }
        }
    }, content = {
        Column(Modifier.padding(it)) {
            HorizontalPager(state = pagerState) { index ->
                if (index == 2) {
                    // Terza pagina con campi obbligatori
                    ThirdPageContent(
                        firstName = firstName,
                        lastName = lastName,
                        age = age,
                        sex = sex,
                        weight = weight,
                        height = height,
                        nationality = nationality
                    )
                } else {
                    OnboardingGraphUI(onboardingModel = pages[index])
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
    nationality: MutableState<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top, // Imposta la disposizione verticale (puoi cambiare se necessario)
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
            text = "I will use info such age, height, etc due to a more accurate calories analysis",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = firstName.value,
            onValueChange = { if (it.length <= 15) firstName.value = it },
            label = { Text("First Name") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = lastName.value,
            onValueChange = { if (it.length <= 15) lastName.value = it },
            label = { Text("Last Name") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = age.value,
            onValueChange = { age.value = it.filter { char -> char.isDigit() } },
            label = { Text("Age") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = weight.value,
            onValueChange = { weight.value = it },
            label = { Text("Weight (kg)") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = height.value,
            onValueChange = { height.value = it },
            label = { Text("Height (cm)") },
            modifier = Modifier.clip(RoundedCornerShape(26.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        NationalitySelector(nationality = nationality)
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