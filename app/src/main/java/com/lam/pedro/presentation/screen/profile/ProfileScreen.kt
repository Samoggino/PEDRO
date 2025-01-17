package com.lam.pedro.presentation.screen.profile

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.R
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.screen.profile.ProfilePreference.AGE
import com.lam.pedro.presentation.screen.profile.ProfilePreference.FIRST_NAME
import com.lam.pedro.presentation.screen.profile.ProfilePreference.HEIGHT
import com.lam.pedro.presentation.screen.profile.ProfilePreference.LAST_NAME
import com.lam.pedro.presentation.screen.profile.ProfilePreference.NATIONALITY
import com.lam.pedro.presentation.screen.profile.ProfilePreference.SEX
import com.lam.pedro.presentation.screen.profile.ProfilePreference.WEIGHT

data class ProfileField(
    val label: String,
    val value: String,
    val fieldName: ProfilePreference
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavBack: () -> Unit,
    titleId: Int,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
) {
    // Recupera i dati da ViewModel
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val age by viewModel.age.collectAsState()
    val sex by viewModel.sex.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val height by viewModel.height.collectAsState()
    val nationality by viewModel.nationality.collectAsState()

    // Crea una lista di oggetti ProfileField
    val profileFields = listOf(
        ProfileField("First Name", firstName, FIRST_NAME),
        ProfileField("Last Name", lastName, LAST_NAME),
        ProfileField("Age", age, AGE),
        ProfileField("Sex", sex, SEX),
        ProfileField("Weight (kg)", weight, WEIGHT),
        ProfileField("Height (m)", height, HEIGHT),
        ProfileField("Nationality", nationality, NATIONALITY)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = { BackButton { onNavBack() } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
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
            Image(
                painter = painterResource(id = R.drawable.profile_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(profileFields) { profileField ->
                    ProfileDetail(
                        label = profileField.label,
                        value = profileField.value,
                        profilePreference = profileField.fieldName,
                        onValueChange = {
                            viewModel.updateProfileField(profileField.fieldName, it)
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
    profilePreference: ProfilePreference,
    onValueChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val tempValue by remember { mutableStateOf(value) }

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
                modifier = Modifier.size(30.dp)
            )
        }
    }

    if (showDialog) {
        ProfileDialog(
            label = label,
            value = tempValue,
            profilePreference = profilePreference,
            onValueChange = { newValue ->
                onValueChange(newValue)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ProfileDialog(
    label: String,
    value: String,
    profilePreference: ProfilePreference,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempValue by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = when (profilePreference) {
            NATIONALITY -> "Select your nationality"
            else -> "Edit $label"
        }) },
        text = {
            when (profilePreference) {
                FIRST_NAME, LAST_NAME, AGE, WEIGHT, HEIGHT -> {
                    val keyboardType = if (profilePreference in listOf(AGE, WEIGHT, HEIGHT)) {
                        KeyboardType.Number
                    } else {
                        KeyboardType.Text
                    }

                    TextField(
                        value = tempValue,
                        onValueChange = { newValue ->
                            if (profilePreference == AGE && newValue.toIntOrNull() != null ||
                                profilePreference != AGE && newValue.toDoubleOrNull() != null ||
                                newValue.isEmpty()) {
                                tempValue = newValue
                            }
                        },
                        label = { Text("Enter new $label") },
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        modifier = Modifier.clip(RoundedCornerShape(26.dp))
                    )
                }
                NATIONALITY -> {
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

                    LazyColumn {
                        items(countries) { country ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tempValue = country
                                        onValueChange(tempValue)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val flag = countryFlags[country] ?: ""
                                Text(
                                    text = flag,
                                    modifier = Modifier.padding(end = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = country,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                SEX -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Male" to "Male", "Female" to "Female").forEach { (label, value) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { tempValue = value }
                            ) {
                                RadioButton(
                                    selected = tempValue == value,
                                    onClick = { tempValue = value }
                                )
                                Text(text = label)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onValueChange(tempValue) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
