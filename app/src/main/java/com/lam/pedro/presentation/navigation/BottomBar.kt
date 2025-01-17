package com.lam.pedro.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.lam.pedro.R

@Composable
fun BottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToActivities: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToMore: () -> Unit,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.graphicsLayer {
            shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            clip = true
        }
    ) {
        Screen.entries.filter { (it.hasMenuItem) }.forEach { item ->

            val selected = item.route == currentRoute
            NavigationBarItem(
                icon = {
                    when (item.titleId) {
                        R.string.home_screen -> Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                        )

                        R.string.activity_list -> Icon(
                            Icons.Filled.SpaceDashboard,
                            contentDescription = "Activities",
                        )

                        R.string.community_screen -> Icon(
                            Icons.Filled.PeopleAlt,
                            contentDescription = "Community",
                        )

                        R.string.more_screen -> Icon(
                            Icons.Filled.MoreHoriz,
                            contentDescription = "More",
                        )
                    }
                },

                label = {
                    Text(
                        text = stringResource(item.titleId),
                        fontWeight = if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                selected = item.route == currentRoute,
                onClick = {
                    Log.d("BottomBar", "Navigating to ${item.route}")
                    when (item.route) {
                        Screen.HomeScreen.route -> onNavigateToHome()
                        Screen.ActivitiesScreen.route -> onNavigateToActivities()
                        Screen.CommunityScreen.route -> onNavigateToCommunity()
                        Screen.MoreScreen.route -> onNavigateToMore()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary, /*Color(0xFFE4B53F)*/
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = Color.White,
                )// Chiama la funzione di navigazione per Home
            )
        }
    }
}
