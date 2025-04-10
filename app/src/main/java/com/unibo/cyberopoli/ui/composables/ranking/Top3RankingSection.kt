package com.unibo.cyberopoli.ui.composables.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.RankingUser

@Composable
fun Top3RankingSection(users: List<RankingUser>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        users.forEach { user ->
            val avatarRes = when (user.avatarUrl) {
                "avatar_male_1" -> R.drawable.avatar_male_1
                "avatar_male_2" -> R.drawable.avatar_male_2
                "avatar_female_1" -> R.drawable.avatar_female_1
                "avatar_female_2" -> R.drawable.avatar_female_2
                else -> R.drawable.avatar_male_1
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(avatarRes), //rememberAsyncImagePainter(user.avatarUrl),
                    contentDescription = stringResource(R.string.avatar),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                if (user.rank == 1) {
                    Text(
                        text = "\uD83D\uDC51", style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = user.name + " " + user.surname,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "${user.score} pt", style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
