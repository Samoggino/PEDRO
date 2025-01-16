package com.lam.pedro.presentation.screen.activities.activitiyscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.component.SessionHistoryRow


@Composable
fun SessionHistory(
    sessionList: List<GenericActivity>, activityEnum: ActivityEnum,
    viewModel: ActivitySessionViewModel
) {
    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .height(350.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        if (sessionList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.empty_history),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(
                sessionList,
            ) { session ->
                Pair(session.basicActivity.startTime, session.basicActivity.endTime)
                SessionHistoryRow(
                    color = activityEnum.color,
                    image = activityEnum.image,
                    session = session,
                    viewModel = viewModel
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF606060)
                )
            }
        }
    }
}