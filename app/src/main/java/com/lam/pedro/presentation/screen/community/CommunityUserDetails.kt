package com.lam.pedro.presentation.screen.community

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.component.ShowSessionDetails
import com.lam.pedro.presentation.serialization.MyRecordsViewModel
import com.lam.pedro.presentation.serialization.MyScreenRecordsFactory
import com.lam.pedro.util.placeholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CommunityUserDetails(
    userUUID: String,
    navController: NavController,
) {

    val rememberedUserUUID = remember(userUUID) { userUUID }

    // Fetch data una sola volta per il nuovo userUUID
    LaunchedEffect(key1 = userUUID) {
        Log.i("Community", "Fetching details for $userUUID")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "User Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Pass paddingValues se usi Material3 correttamente
        CommunityUserDetailsContent(
            userUUID = rememberedUserUUID,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun CommunityUserDetailsContent(
    userUUID: String,
    paddingValues: PaddingValues,
    viewModel: CommunityUserDetailsViewModel = viewModel(factory = CommunityUserDetailsViewModelFactory())

) {
    val activityMap by viewModel.activityMap.collectAsState(emptyMap())
    ActivityHistoryPopup(activityMap, userUUID)
}


@Composable
fun ActivityHistoryPopup(
    activityMap: Map<ActivityEnum, List<GenericActivity>>,
    userUUID: String
) {
    val viewModel: CommunityUserDetailsViewModel =
        viewModel(factory = CommunityUserDetailsViewModelFactory())
    var showDialog by remember { mutableStateOf(false) }
    var selectedActivityType: ActivityEnum? by remember { mutableStateOf(null) }
    val isLoading by viewModel.isFetchingMap.collectAsState()

    // Evita fetch multipli
    LaunchedEffect(key1 = userUUID) {
        Log.i("Community", "Fetching activity map for $userUUID")
        viewModel.fetchActivityMap(userUUID)
    }

    Column {
        Text(text = "Activity History", modifier = Modifier.padding(16.dp))

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .height(300.dp)
                .placeholder(isLoading = isLoading, showShimmerAnimation = true)
        ) {
            items(activityMap.keys.toList()) { activityType ->
                Text(
                    text = activityType.name,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            selectedActivityType = activityType
                            showDialog = true
                        }
                )
            }
        }

        if (showDialog && selectedActivityType != null) {
            val recentActivities = activityMap[selectedActivityType]?.takeLast(5) ?: emptyList()
            ActivityDetailsDialog(
                activities = recentActivities,
                onDismiss = { showDialog = false },
                activityType = selectedActivityType!!
            )
        }
    }
}

@Composable
fun ActivityDetailsDialog(
    activities: List<GenericActivity>,
    activityType: ActivityEnum,
    onDismiss: () -> Unit,
) {

//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text(text = "Recent ${activityType.name} Activities") },
//        text = {
//            LazyColumn(
//                modifier = Modifier.padding(8.dp).heightIn(max = 300.dp)
//            ) {
//                try {
//                    items(activities) { activity ->
//                        Text(text = "Date: ${activity.basicActivity.startTime}, Duration: ${activity.basicActivity.durationInMinutes()}")
//                    }
//                } catch (e: Exception) {
//                    Log.e("Community", "Error showing dialog", e)
//                }
//            }
//        },
//        confirmButton = {
//            Button(onClick = onDismiss) {
//                Text("Close")
//            }
//        }
//    )

    LaunchedEffect(Unit) {
        Log.i("Community", "ActivityDetailsDialog")
    }

    Column(
        modifier = Modifier.heightIn(max = 300.dp)
    ) {
        activities.forEach { activity ->
            ShowSessionDetails(activity)
        }
    }


}

class CommunityUserDetailsViewModel : ViewModel() {
    val activityMap = MutableStateFlow<Map<ActivityEnum, List<GenericActivity>>>(emptyMap())
    val isFetchingMap = MutableStateFlow(false)

    val viewModel = MyScreenRecordsFactory().create(MyRecordsViewModel::class.java)

    fun fetchActivityMap(userUUID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isFetchingMap.value = true

            activityMap.value = viewModel.getActivityMap(userUUID = userUUID)
            isFetchingMap.value = false
        }
    }
}

class CommunityUserDetailsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityUserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityUserDetailsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}