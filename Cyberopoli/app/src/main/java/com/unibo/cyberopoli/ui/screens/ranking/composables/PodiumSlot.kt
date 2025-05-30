package com.unibo.cyberopoli.ui.screens.ranking.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User

@SuppressLint("DiscouragedApi")
@Composable
fun PodiumSlot(
    user: User,
    rank: Int,
    podiumColor: Color,
    medalEmoji: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val podiumHeight =
        when (rank) {
            1 -> 120.dp
            2 -> 90.dp
            3 -> 60.dp
            else -> 40.dp
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = medalEmoji,
            fontSize = if (rank == 1) 24.sp else 20.sp,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        val resId =
            remember(user.avatarUrl) {
                context.resources.getIdentifier(
                    user.avatarUrl,
                    "drawable",
                    context.packageName,
                )
            }
        Image(
            painter = painterResource(resId),
            contentDescription = stringResource(R.string.avatar),
            modifier =
                Modifier
                    .size(if (rank == 1) 64.dp else 54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Text(
            text = user.username,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
        )

        Text(
            text = "${user.totalScore} pt",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
        )

        Box(
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .width(64.dp)
                    .height(podiumHeight)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(podiumColor),
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.7f),
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 4.dp),
            )
        }
    }
}
