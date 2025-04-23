package com.unibo.cyberopoli.ui.composables.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import com.unibo.cyberopoli.data.models.auth.UserData

@Composable
fun Top3RankingSection(users: List<UserData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        users.sortedBy { it.level }.forEach { user ->
            val (avatarSize, offsetY) = when (user.level) {
                1 -> 80.dp to (-16).dp
                2 -> 70.dp to (-8).dp
                3 -> 60.dp to 0.dp
                else -> 60.dp to 0.dp
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .offset(y = offsetY),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        when (user.profileImageUrl) {
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
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                if (user.level == 1) {
                    Text(
                        text = "\uD83D\uDC51", style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "${user.name} ${user.surname}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "${user.score} pt", style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
