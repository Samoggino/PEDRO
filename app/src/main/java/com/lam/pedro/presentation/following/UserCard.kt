package com.lam.pedro.presentation.following

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lam.pedro.presentation.screen.loginscreen.User

// Costanti
private val IconSize = 70.dp
private val RoundedCornerSize = 12.dp
private val RoundedIconSize = 24.dp
private val FollowButtonSize = IconSize * 0.45f
private val PaddingRow = 16.dp
private val NameHeight = 24.dp
private const val AnimationDuration = 2500

@Preview
@Composable
fun UserCardPreview() {
    UserCard(
        user = User(
            id = "1",
            email = "simosamoggia@gmail.com",
            avatarUrl = "https://tfgeogkbrvekrzsgpllc.supabase.co/storage/v1/object/public/avatars/0539f7b7-ec21-4bd1-a8fe-bc8deb5e5260?t=2024-11-21T16%3A48%3A08.264Z"
        ),
        isFollowing = true,
        onClick = {}
    )
}

@Composable
fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(
        targetValue = if (isFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = AnimationDuration), label = ""
    )
    Icon(
        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
        contentDescription = if (isFollowing) "Followed" else "Follow",
        tint = iconColor,
        modifier = Modifier
            .size(FollowButtonSize)
            .clickable { onClick() }
    )
}

@Composable
fun UserCard(user: User, isFollowing: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(RoundedCornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingRow),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            user.avatarUrl?.let { Avatar(it) }
            UserBody(user, isFollowing)
            FollowButton(isFollowing, onClick)
        }
    }
}

@Composable
private fun UserBody(user: User, isFollowing: Boolean) {
    Column(verticalArrangement = Arrangement.Center) {
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isFollowing) "Following" else "Not Following",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun Avatar(avatarUrl: String) {
    if (avatarUrl.isNotBlank()) {
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
                .clip(RoundedCornerShape(RoundedIconSize))
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Default Avatar",
            modifier = Modifier
                .size(IconSize)
                .clip(RoundedCornerShape(RoundedIconSize)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun shimmerAnimation(): Brush {
    val transition = rememberInfiniteTransition(label = "")
    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AnimationDuration,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    return Brush.linearGradient(
        colors = listOf(
            Color.DarkGray.copy(alpha = 0.4f),
            Color.Black.copy(alpha = 0.2f),
            Color.DarkGray.copy(alpha = 0.4f)
        ),
        start = Offset.Zero,
        end = Offset(shimmerTranslate, shimmerTranslate)
    )
}

@Composable
fun UserPlaceholder(alpha: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RoundedCornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingRow),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize)
                    .clip(RoundedCornerShape(RoundedCornerSize))
                    .background(shimmerAnimation())
                    .graphicsLayer(alpha = alpha)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box(
                    modifier = Modifier
                        .height(NameHeight)
                        .fillMaxWidth()
                        .background(shimmerAnimation())
                        .graphicsLayer(alpha = alpha)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                        .background(shimmerAnimation())
                        .graphicsLayer(alpha = alpha)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(FollowButtonSize)
                    .clip(RoundedCornerShape(RoundedCornerSize))
                    .background(shimmerAnimation())
                    .graphicsLayer(alpha = alpha)
            )
        }
    }
}


