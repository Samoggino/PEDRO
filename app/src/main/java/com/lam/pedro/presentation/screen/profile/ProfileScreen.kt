package com.lam.pedro.presentation.screen.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.lam.pedro.R

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    titleId: Int
) {

    // Ottieni il contesto
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

    // Inizializza le variabili con i dati salvati o valori di default
    var firstName by remember {
        mutableStateOf(
            sharedPreferences.getString("firstName", "Angela") ?: "Angela"
        )
    }
    var lastName by remember {
        mutableStateOf(
            sharedPreferences.getString("lastName", "Taylor") ?: "Taylor"
        )
    }
    var age by remember { mutableStateOf(sharedPreferences.getString("age", "27") ?: "27") }
    var weight by remember {
        mutableStateOf(
            sharedPreferences.getString("weight", "70.5") ?: "70.5"
        )
    }
    var height by remember {
        mutableStateOf(
            sharedPreferences.getString("height", "1.75") ?: "1.75"
        )
    }
    var nationality by remember {
        mutableStateOf(
            sharedPreferences.getString(
                "nationality",
                "American"
            ) ?: "American"
        )
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icona in alto
            Image(
                painter = painterResource(id = R.drawable.profile_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            LazyColumn {
                item {
                    // Dati del profilo
                    ProfileDetail(
                        label = "First Name",
                        value = firstName,
                        onValueChange = { newValue ->
                            firstName = newValue
                            saveToPreferences(sharedPreferences, "firstName", newValue)
                        }
                    )
                    ProfileDetail(
                        label = "Last Name",
                        value = lastName,
                        onValueChange = { newValue ->
                            lastName = newValue
                            saveToPreferences(sharedPreferences, "lastName", newValue)
                        }
                    )
                    ProfileDetail(
                        label = "Age",
                        value = age,
                        onValueChange = { newValue ->
                            age = newValue
                            saveToPreferences(sharedPreferences, "age", newValue)
                        }
                    )
                    ProfileDetail(
                        label = "Weight (kg)",
                        value = weight,
                        onValueChange = { newValue ->
                            weight = newValue
                            saveToPreferences(sharedPreferences, "weight", newValue)
                        }
                    )
                    ProfileDetail(
                        label = "Height (m)",
                        value = height,
                        onValueChange = { newValue ->
                            height = newValue
                            saveToPreferences(sharedPreferences, "height", newValue)
                        }
                    )
                    ProfileDetail(
                        label = "Nationality",
                        value = nationality,
                        onValueChange = { newValue ->
                            nationality = newValue
                            saveToPreferences(sharedPreferences, "nationality", newValue)
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun ProfileDetail(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempValue by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clickable { showDialog = true }
                .align(Alignment.CenterVertically),
        ) {
            Image(
                painter = painterResource(id = R.drawable.modify_icon),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Edit $label") },
            text = {
                TextField(
                    value = tempValue,
                    onValueChange = { tempValue = it },
                    label = { Text(text = "Enter new $label") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(tempValue)
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun saveToPreferences(sharedPreferences: SharedPreferences, key: String, value: String) {
    sharedPreferences.edit()
        .putString(key, value)
        .apply()
}

 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    titleId: Int,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current))
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icona in alto
            Image(
                painter = painterResource(id = R.drawable.profile_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            LazyColumn {
                item {
                    // Dati del profilo
                    ProfileDetail(
                        label = "First Name",
                        value = profileViewModel.firstName,
                        onValueChange = { profileViewModel.updateProfileField("firstName", it) }
                    )
                    ProfileDetail(
                        label = "Last Name",
                        value = profileViewModel.lastName,
                        onValueChange = { profileViewModel.updateProfileField("lastName", it) }
                    )
                    ProfileDetail(
                        label = "Age",
                        value = profileViewModel.age,
                        onValueChange = { profileViewModel.updateProfileField("age", it) }
                    )
                    ProfileDetail(
                        label = "Sex",
                        value = profileViewModel.sex,
                        onValueChange = { profileViewModel.updateProfileField("sex", it) }
                    )
                    ProfileDetail(
                        label = "Weight (kg)",
                        value = profileViewModel.weight,
                        onValueChange = { profileViewModel.updateProfileField("weight", it) }
                    )
                    ProfileDetail(
                        label = "Height (m)",
                        value = profileViewModel.height,
                        onValueChange = { profileViewModel.updateProfileField("height", it) }
                    )
                    ProfileDetail(
                        label = "Nationality",
                        value = profileViewModel.nationality,
                        onValueChange = { profileViewModel.updateProfileField("nationality", it) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileDetail(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempValue by remember { mutableStateOf(value) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clickable { showDialog = true }
                .align(Alignment.CenterVertically),
        ) {
            Image(
                painter = painterResource(id = R.drawable.modify_icon),
                contentDescription = null,
                modifier = Modifier.size(35.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Edit $label") },
            text = {
                TextField(
                    value = tempValue,
                    onValueChange = { tempValue = it },
                    label = { Text(text = "Enter new $label") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(tempValue)
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}