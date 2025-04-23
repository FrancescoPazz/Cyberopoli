package com.unibo.cyberopoli.ui.screens.ranking.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unibo.cyberopoli.data.models.auth.UserData

@Composable
fun RankingListSection(users: List<UserData>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        users.forEach { user ->
            RankingListItem(user)
            HorizontalDivider()
        }
    }
}

