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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.R
import com.lam.pedro.presentation.component.BackButton

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
        ProfileField("First Name", firstName, ProfilePreference.FIRST_NAME),
        ProfileField("Last Name", lastName, ProfilePreference.LAST_NAME),
        ProfileField("Age", age, ProfilePreference.AGE),
        ProfileField("Sex", sex, ProfilePreference.SEX),
        ProfileField("Weight (kg)", weight, ProfilePreference.WEIGHT),
        ProfileField("Height (m)", height, ProfilePreference.HEIGHT),
        ProfileField("Nationality", nationality, ProfilePreference.NATIONALITY)
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
                modifier = Modifier.size(30.dp)
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
                    label = { Text(text = "Enter new $label") },
                    modifier = Modifier.clip(RoundedCornerShape(26.dp))
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
