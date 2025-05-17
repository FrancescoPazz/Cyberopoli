package com.unibo.cyberopoli.ui.screens.ranking.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User

@Composable
fun Top3RankingSection(users: List<User>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(top = 20.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.ranking),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (users.size > 1) {
                PodiumSlot(
                    user = users[1],
                    rank = 2,
                    podiumColor = Color(0xFFC0C0C0),
                    medalEmoji = "🥈"
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (users.isNotEmpty()) {
                PodiumSlot(
                    user = users[0],
                    rank = 1,
                    podiumColor = Color(0xFFFFD700),
                    medalEmoji = "🥇👑"
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (users.size > 2) {
                PodiumSlot(
                    user = users[2],
                    rank = 3,
                    podiumColor = Color(0xFFCD7F32),
                    medalEmoji = "🥉"
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RowScope.PodiumSlot(
    user: User,
    rank: Int,
    podiumColor: Color,
    medalEmoji: String
) {
    val avatarSize = when (rank) {
        1 -> 88.dp
        2 -> 72.dp
        else -> 64.dp
    }
    val columnHeightRatio = when (rank) {
        1 -> 1.8f
        2 -> 1.45f
        else -> 1f
    }
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(columnHeightRatio)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = medalEmoji,
            fontSize = if (rank == 1) 32.sp else 26.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Image(
            painter = painterResource(
                when (user.avatarUrl) {
                    "avatar_male_1" -> R.drawable.avatar_male_1
                    "avatar_male_2" -> R.drawable.avatar_male_2
                    "avatar_female_1" -> R.drawable.avatar_female_1
                    "avatar_female_2" -> R.drawable.avatar_female_2
                    else -> R.drawable.avatar_male_1
                }
            ),
            contentDescription = stringResource(R.string.avatar),
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(3.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.username,
            style = if (rank == 1) typography.titleMedium else typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = "${user.totalScore} pt",
            style = if (rank == 1) typography.bodyMedium else typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(if (rank == 1) 50.dp else if (rank == 2) 40.dp else 30.dp)
                .background(podiumColor, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        ) {
            Text(
                text = "$rank",
                modifier = Modifier.align(Alignment.Center),
                fontSize = if (rank == 1) 20.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}