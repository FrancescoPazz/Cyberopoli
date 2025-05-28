package com.unibo.cyberopoli.ui.screens.ranking.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.CyberopoliCard

@Composable
fun Top3RankingSection(users: List<User>) {
    CyberopoliCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = 20.dp,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Top 3",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            val slotModifier = Modifier.weight(1f)

            if (users.size > 1) {
                PodiumSlot(
                    user = users[1], rank = 2, podiumColor = Color(0xFFC0C0C0), medalEmoji = "🥈",
                    modifier = slotModifier
                )
            } else {
                Box(modifier = slotModifier)
            }

            if (users.isNotEmpty()) {
                PodiumSlot(
                    user = users[0], rank = 1, podiumColor = Color(0xFFFFD700), medalEmoji = "🥇👑",
                    modifier = slotModifier
                )
            } else {
                Box(modifier = slotModifier)
            }

            if (users.size > 2) {
                PodiumSlot(
                    user = users[2], rank = 3, podiumColor = Color(0xFFCD7F32), medalEmoji = "🥉",
                    modifier = slotModifier
                )
            } else {
                Box(modifier = slotModifier)
            }
        }
    }
}