package com.unibo.cyberopoli.ui.screens.ranking.view.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.CyberopoliGradientCard

@SuppressLint("DiscouragedApi")
@Composable
fun MyRankingPositionCard(
    user: User,
    myRank: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    CyberopoliGradientCard(
        modifier = modifier.padding(horizontal = 16.dp),
        gradientColors =
            listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
            ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = 12.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "$myRank#",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(60.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

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
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.my_rank),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "${user.totalScore} pt",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
