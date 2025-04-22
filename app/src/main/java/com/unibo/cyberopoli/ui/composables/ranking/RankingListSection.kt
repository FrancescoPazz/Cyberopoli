package com.unibo.cyberopoli.ui.composables.ranking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unibo.cyberopoli.data.models.RankingUser

@Composable
fun RankingListSection(users: List<RankingUser>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        users.forEach { user ->
            RankingListItem(user)
            HorizontalDivider()
        }
    }
}

