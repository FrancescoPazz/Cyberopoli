package com.unibo.cyberopoli.ui.screens.ranking.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.ui.components.CyberopoliCard

@Composable
fun RankingListCard(
    users: List<User>,
    currentUser: User?,
    rankOffset: Int
) {
    CyberopoliCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = 0.dp,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.ranking),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        users.forEachIndexed { index, user ->
            val currentRank = rankOffset + index + 1
            RankingListItem(
                user = user,
                rank = currentRank,
                isCurrentUser = user.id == currentUser?.id
            )
            if (index < users.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}