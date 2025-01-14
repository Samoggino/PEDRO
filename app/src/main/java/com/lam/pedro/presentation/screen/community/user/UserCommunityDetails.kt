package com.lam.pedro.presentation.screen.community.user

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseRepositoryImpl
import com.lam.pedro.presentation.component.ShowSessionDetails

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserCommunityDetails(
    selectedUser: String,
    onNavBack: () -> Unit,
) {

    LaunchedEffect(key1 = selectedUser) {
        Log.i("Community", "Fetching details for $selectedUser")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "User Details") },
                navigationIcon = {
                    IconButton(onClick = { onNavBack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        // Pass paddingValues se usi Material3 correttamente
        CommunityUserDetailsContent(
            userUUID = selectedUser,
            paddingValues = paddingValues,
        )
    }
}

@Composable
fun CommunityUserDetailsContent(
    userUUID: String,
    paddingValues: PaddingValues,
    viewModel: UserCommunityDetailsViewModel = viewModel(
        factory = UserCommunityDetailsViewModelFactory(
            userUUID,
            ActivitySupabaseRepositoryImpl() // Passaggio del repository al ViewModel
        )
    )

) {
    val activityMap by viewModel.activityMap.collectAsState(emptyMap())
    ActivityHistoryPopup(activityMap, userUUID, paddingValues, viewModel)
}

@Composable
fun ActivityHistoryPopup(
    activityMap: Map<ActivityEnum, List<GenericActivity>>,
    userUUID: String,
    paddingValues: PaddingValues,
    viewModel: UserCommunityDetailsViewModel = viewModel(
        factory = UserCommunityDetailsViewModelFactory(
            userUUID,
            ActivitySupabaseRepositoryImpl() // Passaggio del repository al ViewModel
        )
    )
) {
    val isLoading by viewModel.isLoading.collectAsState() // Osserva lo stato di caricamento
    var showDialog by remember { mutableStateOf(false) }
    var selectedActivityType: ActivityEnum? by remember { mutableStateOf(null) }

    // Evita fetch multipli
    LaunchedEffect(key1 = userUUID) {
        Log.i("Community", "Fetching activity map for $userUUID")
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {

        if (activityMap.isEmpty()) {
            Log.i("Community", "No activity found for $userUUID")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No activity found", style = MaterialTheme.typography.bodyMedium)
            }
            return
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Select an activity",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(activityMap.keys.filter { activityType ->
                    // Mostra solo attività con record disponibili
                    !activityMap[activityType].isNullOrEmpty()
                }.toList()) { activityType ->
                    ActivityCard(activityType = activityType) {
                        selectedActivityType = activityType
                        showDialog = true
                    }
                }
            }

            // Mostra il bottom sheet se richiesto
            if (showDialog && selectedActivityType != null) {
                val recentActivities = activityMap[selectedActivityType]?.takeLast(5) ?: emptyList()
                ActivityDetailsBottomSheet(
                    activities = recentActivities,
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activityType: ActivityEnum,
    onCardClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp)) // Clip per rispettare il bordo arrotondato
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current // Usa l'indicazione ripple predefinita
            ) {
                onCardClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(26.dp) // Arrotonda gli angoli della Card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = activityType.image),
                contentDescription = activityType.name,
                tint = activityType.color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = activityType.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsBottomSheet(
    activities: List<GenericActivity>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Recent Activities",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            activities.forEach { activity ->
                ShowSessionDetails(activity)
            }
            Spacer(modifier = Modifier.height(16.dp)) // Add some space at the bottom
        }
    }
}