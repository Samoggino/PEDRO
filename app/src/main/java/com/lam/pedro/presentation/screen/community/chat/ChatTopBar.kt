package com.lam.pedro.presentation.screen.community.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lam.pedro.presentation.component.Avatar
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.screen.more.loginscreen.User

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatTopBar(
    selectedUser: User,
    onNavBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Avatar(
                    avatarUrl = selectedUser.avatarUrl,
                    size = 40.dp,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(selectedUser.username)

            }

        },
        colors = TopAppBarDefaults.topAppBarColors(),
        navigationIcon = { BackButton { onNavBack() } },
    )
}