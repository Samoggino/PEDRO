/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lam.pedro.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import com.lam.pedro.data.SleepSessionData
import com.example.healthconnectsample.data.dateTimeWithOffsetOrDefault
import com.example.healthconnectsample.data.formatHoursMinutes
import com.lam.pedro.R
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Creates a row to represent a [SleepSessionData], which encompasses data for both the sleep
 * session and any fine-grained sleep stages.
 */
@Composable
fun SleepSessionRow(
    sessionData: SleepSessionData,
    startExpanded: Boolean = false
) {
    val startZonedDateTime = ZonedDateTime.parse(sessionData.startTime)
    val endZonedDateTime = ZonedDateTime.parse(sessionData.endTime)
    val endZonedOffset = endZonedDateTime.offset
    val sessionToDuration = Duration.between(startZonedDateTime.toInstant(), endZonedDateTime.toInstant())


    var expanded by remember { mutableStateOf(startExpanded) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable { expanded = !expanded },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val formatter = DateTimeFormatter.ofPattern("eee, d LLL")
        val startDateTime =
            dateTimeWithOffsetOrDefault(startZonedDateTime.toInstant(), endZonedOffset)
        Text(
            modifier = Modifier.weight(0.4f),
            color = MaterialTheme.colorScheme.primary,
            text = startDateTime.format(formatter)
        )
        if (!expanded) {
            Text(
                modifier = Modifier.weight(0.4f),
                text = sessionToDuration?.formatHoursMinutes()
                    ?: stringResource(id = R.string.not_available_abbrev)
            )
        }
        IconButton(onClick = { expanded = !expanded }) {
            val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
            Icon(icon, contentDescription = stringResource(R.string.delete_button))
        }
    }
    if (expanded) {
        val startZonedDateTime = ZonedDateTime.parse(sessionData.startTime)
        val endZonedDateTime = ZonedDateTime.parse(sessionData.endTime)

        val startEndLabel = formatDisplayTimeStartEnd(
            startZonedDateTime,
            endZonedDateTime
        )
        SleepSessionDetailRow(labelId = R.string.sleep_time, item = startEndLabel)
        SleepSessionDetailRow(
            labelId = R.string.sleep_duration,
            item = sessionToDuration?.formatHoursMinutes()
        )
        SleepSessionDetailRow(labelId = R.string.sleep_notes, item = sessionData.notes)
        if (sessionData.stages.isNotEmpty()) {
            SleepSessionDetailRow(labelId = R.string.sleep_stages, item = "")
            SleepStagesDetail(sessionData.toDisplayStages())
        }
    }


}

data class StageDisplay(
    val stageType: String,
    val startTime: String,
    val endTime: String
)

fun SleepSessionData.toDisplayStages(): List<StageDisplay> {
    return stages.map {
        StageDisplay(
            stageType = when (it.stage) {
                SleepSessionRecord.STAGE_TYPE_DEEP -> "Deep"
                SleepSessionRecord.STAGE_TYPE_LIGHT -> "Light"
                SleepSessionRecord.STAGE_TYPE_REM -> "REM"
                else -> "Unknown"
            },
            startTime = it.startTime.toString(),
            endTime = it.endTime.toString()
        )
    }
}

@Composable
fun SleepStagesDetail(stages: List<StageDisplay>) {
    Column {
        stages.forEach { stage ->
            Row {
                Text(text = "Stage: ${stage.stageType}")
                Text(text = "Start: ${stage.startTime}")
                Text(text = "End: ${stage.endTime}")
            }
        }
    }
}

fun formatDisplayTimeStartEnd(start: ZonedDateTime, end: ZonedDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm z")
    return "${start.format(formatter)} - ${end.format(formatter)}"
}

