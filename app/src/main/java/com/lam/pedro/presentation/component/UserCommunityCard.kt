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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lam.pedro.data.datasource.SecurePreferencesManager.getAvatarUrl
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.util.placeholder

const val ANIMATION_TIME = 1000
val ICON_SIZE = 70.dp
val FOLLOW_BUTTON_SIZE = ICON_SIZE * 0.45f
val NameHeight = 24.dp

@Composable
fun UserCommunityCard(
    user: User,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onActivityButton: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val roundedCornerSize = 16.dp

    Card(
        modifier = modifier
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

            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActivitiesButton(onActivityButton)

                // Follow Button
                MessageButton(user) {
                    onChatClick()
                }

                FollowButton(isFollowing, onFollowClick)
            }
        }
    }
}

@Composable
private fun ActivitiesButton(onActivityButtonClick: () -> Unit) {
    IconButton(onClick = {
        onActivityButtonClick()
    }, modifier = Modifier.size(24.dp)) {
        Icon(
            imageVector = Icons.Default.SpaceDashboard,
            contentDescription = "User Activity",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
fun Avatar(
    avatarUrl: String? = getAvatarUrl(),
    size: Dp = ICON_SIZE,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
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
                .size(size)
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
                .size(size)
                .clip(RoundedCornerShape(50)),
            tint = tint
        )
    }
}

@Composable
fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    val iconColor by animateColorAsState(
        targetValue = if (isFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = ANIMATION_TIME), label = ""
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
            .size(FOLLOW_BUTTON_SIZE)
            .scale(scale)
            .clip(CircleShape) // Limita la forma dell'icona
            .clickable(onClick = onClick) // Cliccabile solo sull'icona
            .padding(4.dp) // Padding per mantenere l'area dell'icona
            .animateContentSize()
    )
}


@Composable
fun MessageButton(user: User, onClick: (User) -> Unit) {
    IconButton(
        onClick = { onClick(user) }, // Azione quando si clicca sul pulsante
        modifier = Modifier
            .size(FOLLOW_BUTTON_SIZE)
            .padding(4.dp)
            .animateContentSize()
    ) {
        Icon(
            imageVector = Icons.Default.Mail, // Icona di messaggio
            contentDescription = "Message ${user.username}",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun UserPlaceholder(animation: Float, modifier: Modifier = Modifier) {
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
                    .size(ICON_SIZE)
                    .clip(RoundedCornerShape(50))
                    .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                    .graphicsLayer(alpha = animation)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box(
                    modifier = Modifier
                        .height(NameHeight)
                        .fillMaxWidth()
                        .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                        .graphicsLayer(alpha = animation)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                        .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                        .graphicsLayer(alpha = animation)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(FOLLOW_BUTTON_SIZE)
                    .clip(RoundedCornerShape(50))
                    .placeholder(isLoading, MaterialTheme.colorScheme.onSurfaceVariant)
                    .graphicsLayer(alpha = animation)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}