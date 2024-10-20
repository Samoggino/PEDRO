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
package com.lam.pedro.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBarItem

/**
 * An item in the side navigation drawer.
 */
/*
@Composable
fun BottomBarItem(
    item: Screen,
    selected: Boolean,
    onItemClick: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(item) })
            .height(48.dp)
            .padding(start = 16.dp),
    ) {
        Text(
            text = stringResource(item.titleId),
            style = MaterialTheme.typography.headlineMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground
            }
        )
    }
}

 */
/*
@Composable
fun BottomBarItem(
    item: Screen,
    selected: Boolean,
    onItemClick: () -> Unit // Cambia il tipo per essere una lambda senza parametri
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = item.icon, // Assicurati che Screen abbia un'icona
                contentDescription = stringResource(item.titleId),
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary // Colore dell'icona selezionata
                } else {
                    MaterialTheme.colorScheme.onBackground // Colore dell'icona non selezionata
                }
            )
        },
        label = {
            Text(
                text = stringResource(item.titleId),
                color = if (selected) {
                    MaterialTheme.colorScheme.primary // Colore del testo selezionato
                } else {
                    MaterialTheme.colorScheme.onBackground // Colore del testo non selezionato
                }
            )
        },
        selected = selected,
        onClick = onItemClick // Passa la lambda qui
    )
}

 */
