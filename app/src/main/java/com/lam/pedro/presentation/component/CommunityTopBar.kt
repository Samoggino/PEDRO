package com.lam.pedro.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.lam.pedro.util.vibrateOnClick


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTopBar(
    userIsLogged: Boolean,
    followingOnlyState: MutableState<Boolean>,
    onNavBack: () -> Unit,
) {
    TopAppBar(
        title = { Text("Community") },
        navigationIcon = {
            IconButton(onClick = { onNavBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (userIsLogged) {
                IconButton(
                    onClick = {
                        followingOnlyState.value = !followingOnlyState.value
                        vibrateOnClick()
                    }
                ) {
                    Icon(
                        imageVector = if (followingOnlyState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
        }
    )
}
