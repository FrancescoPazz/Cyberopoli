package com.unibo.cyberopoli.ui.screens.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User

@Composable
fun PlayerWelcomeCard(user: User?, modifier: Modifier = Modifier) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
    )
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = cardColors
    ) {
        Box(modifier = Modifier.background(gradientBrush)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user != null) {
                    val avatarRes = when (user.avatarUrl) {
                        "avatar_male_1" -> R.drawable.avatar_male_1
                        "avatar_male_2" -> R.drawable.avatar_male_2
                        "avatar_female_1" -> R.drawable.avatar_female_1
                        "avatar_female_2" -> R.drawable.avatar_female_2
                        else -> null
                    }
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = stringResource(R.string.avatar, user.username),
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                                .padding(4.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        DefaultAvatarIcon(size = 80.dp)
                    }
                } else {
                    DefaultAvatarIcon(size = 80.dp)
                }


                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (user != null) stringResource(
                            R.string.welcome_back_user,
                            user.username
                        )
                        else stringResource(R.string.welcome_guest),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (user != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.EmojiEvents,
                                contentDescription = stringResource(R.string.level),
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "# ${user.level}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = stringResource(R.string.internet_points),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stringResource(R.string.internet_points)} $${user.totalScore.toCyberopoliCurrency()}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.log_in_to_play_and_see_stats),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DefaultAvatarIcon(size: androidx.compose.ui.unit.Dp) {
    Icon(
        imageVector = Icons.Filled.PersonOutline,
        contentDescription = stringResource(R.string.avatar),
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
            .padding(12.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

fun Int.toCyberopoliCurrency(): String {
    return "%,d".format(this)
}