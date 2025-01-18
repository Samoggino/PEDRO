package com.lam.pedro.presentation.serialization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseSupabaseRepositoryImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreenRecords(
    onNavBack: () -> Unit,
    onCommunityClick: () -> Unit,
    viewModel: MyRecordsViewModel = viewModel(
        factory = MyScreenRecordsFactory(
            activityRepository = ActivitySupabaseSupabaseRepositoryImpl()
        )
    )
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Activity Methods") },
                navigationIcon = {
                    IconButton(onClick = { onNavBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to login"
                        )
                    }
                }

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // List of activities
            ActivityEnum.entries.forEach { activityType ->
                ActivityRow(
                    activityEnum = activityType,
                    onInsertClick = { viewModel.insertActivitySession(activityType) },
                )
            }

            // Navigation buttons
            NavButtons(onCommunityClick)
        }
    }
}

sealed class ResultState {
    data object Idle : ResultState()
    data object Loading : ResultState()
    data object Success : ResultState()
    data object Error : ResultState()
}

@Composable
fun NavButtons(onCommunityClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Button(
            onClick = onCommunityClick,
            content = { Text("Vai alla community dei gringos") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ActivityRow(
    activityEnum: ActivityEnum,
    onInsertClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onInsertClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = activityEnum.color // Cambia il colore del bottone
            )
        ) {
            Text("Insert ${activityEnum.name}")
        }

    }
}
