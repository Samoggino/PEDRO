package com.lam.pedro.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lam.pedro.presentation.screen.community.AnimationDuration
import com.lam.pedro.presentation.screen.community.FollowButtonSize
import com.lam.pedro.presentation.screen.community.IconSize
import com.lam.pedro.presentation.screen.community.NameHeight
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.util.placeholder

@Composable
fun UserCard(user: User, isFollowing: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val roundedCornerSize = 16.dp
    Card(
        modifier = modifier
            .clickable { onClick() }
            .then(
                if (isFollowing) Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(roundedCornerSize)
                ) else Modifier
            ),
        shape = RoundedCornerShape(roundedCornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Avatar(user.avatarUrl)

            Spacer(modifier = Modifier.width(12.dp))

            // User Info (email)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Follow Button
            FollowButton(isFollowing, onClick)
        }
    }
}


@Composable
private fun Avatar(avatarUrl: String?) {
    if (!avatarUrl.isNullOrBlank()) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = avatarUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(IconSize)
                .clip(RoundedCornerShape(50))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(50)
                )
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Default Avatar",
            modifier = Modifier
                .size(IconSize)
                .clip(RoundedCornerShape(50)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(
        targetValue = if (isFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = AnimationDuration), label = ""
    )

    val scale by animateFloatAsState(
        targetValue = if (isFollowing) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    Icon(
        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
        contentDescription = if (isFollowing) "Followed" else "Follow",
        tint = iconColor,
        modifier = Modifier
            .size(FollowButtonSize)
            .scale(scale)
            .clickable { onClick() }
            .padding(4.dp)
            .animateContentSize()
    )
}

@Composable
fun UserPlaceholder(alpha: Float, modifier: Modifier = Modifier) {

    val isLoading = true

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize)
                    .clip(RoundedCornerShape(50))
                    .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                    .graphicsLayer(alpha = alpha)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box(
                    modifier = Modifier
                        .height(NameHeight)
                        .fillMaxWidth()
                        .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                        .graphicsLayer(alpha = alpha)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                        .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                        .graphicsLayer(alpha = alpha)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(FollowButtonSize)
                    .clip(RoundedCornerShape(50))
                    .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                    .graphicsLayer(alpha = alpha)
            )

        }
        Spacer(modifier = Modifier.width(16.dp))
    }


}
