package com.lam.pedro.presentation.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
//import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel

import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lam.pedro.data.activity.GenericActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryRow(
    color: Color,
    image: Int,
    session: GenericActivity,
    viewModel: ActivitySessionViewModel
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.clickable {
        viewModel.selectSession(session) // Seleziona la sessione
        showBottomSheet = true
    }) {
        Row(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = image),
                contentDescription = "Stop",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = session.basicActivity.title, modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.TouchApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Log.d("TEST SESSION TYPE", session.toString())
                ShowSessionDetails(session, color)
            }
        }

    }
}

