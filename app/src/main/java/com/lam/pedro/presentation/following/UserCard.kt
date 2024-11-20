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

val iconSize = 70.dp
val roundedCardCornerSize = 12.dp
val roundedIcon = 24.dp
val followButtonSize = iconSize * 0.45f
val paddingRow = 16.dp
val nameHeight = 24.dp
val animationDuration = 2500

@Composable
private fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    // Icona di follow
    val iconColor by animateColorAsState(
        targetValue = if (isFollowing) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = animationDuration), label = ""
    )

    val icon = if (isFollowing) Icons.Default.Check else Icons.Default.Add
    Icon(
        imageVector = icon,
        contentDescription = if (isFollowing) "Followed" else "Follow",
        tint = iconColor,
        modifier = Modifier
            .size(followButtonSize)
            .clickable { onClick() }
    )
}

@Composable
fun UserCard(user: User, isFollowing: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(roundedCardCornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingRow),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AvatarIcon(user.avatarUrl)
            UserBody(user, isFollowing)
            FollowButton(isFollowing, onClick)
        }
    }
}

@Composable
private fun UserBody(
    user: User,
    isFollowing: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isFollowing) "Following" else "Not Following",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFollowing) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun AvatarIcon(avatarUrl: String) {
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
                .size(iconSize)
                .clip(RoundedCornerShape(roundedIcon))
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Default Avatar",
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(roundedIcon)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun shimmerAnimation(): Brush {
    val transition = rememberInfiniteTransition(label = "")
    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f, // Aumenta il valore per una transizione più fluida e lunga
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration, // Aumenta la durata per rallentare ulteriormente l'animazione
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    return Brush.linearGradient(
        colors = listOf(
            Color.DarkGray.copy(alpha = 0.4f), // Sfumatura più morbida
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
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(roundedCardCornerSize),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingRow),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(roundedCardCornerSize))
                    .background(shimmerAnimation())
                    .graphicsLayer(alpha = alpha)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box( // Nome
                    modifier = Modifier
                        .height(nameHeight)
                        .fillMaxWidth()
                        .background(shimmerAnimation())
                        .graphicsLayer(alpha = alpha)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box( // Stato
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
                    .size(followButtonSize)
                    .clip(RoundedCornerShape(roundedCardCornerSize))
                    .background(shimmerAnimation())
                    .graphicsLayer(alpha = alpha)
            )
        }
    }
}

@Preview
@Composable
fun UserCardPreview() {
    UserCard(
        user = User(
            id = "1",
            email = "simosamoggia@gmail.com",
            avatarUrl = "https://avatars.githubusercontent.com/u/1797357?v=4"
        ),
        isFollowing = true,
        onClick = {}
    )
}


