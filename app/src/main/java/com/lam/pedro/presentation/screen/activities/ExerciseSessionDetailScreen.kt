package com.lam.pedro.presentation.screen.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.presentation.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSessionDetailScreen(
    viewModel: ActivitySessionViewModel,
    navController: NavHostController
) {
    val session by viewModel.selectedSession.observeAsState()



    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        text = "${session?.title} detail" ?: "Session detail",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    BackButton(navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            session?.let {
                // Visualizza i dettagli della sessione
                it.title?.let { it1 -> Text(text = it1) }
                // Altri dettagli...
            }

        }
    }
}