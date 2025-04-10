package com.unibo.cyberopoli.ui.composables.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun MyRankingPosition(user: RankingUser) {
    val avatarRes = when (user.avatarUrl) {
        "avatar_male_1" -> R.drawable.avatar_male_1
        "avatar_male_2" -> R.drawable.avatar_male_2
        "avatar_female_1" -> R.drawable.avatar_female_1
        "avatar_female_2" -> R.drawable.avatar_female_2
        else -> R.drawable.avatar_male_1
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(avatarRes),//rememberAsyncImagePainter(user.avatarUrl),
            contentDescription = stringResource(R.string.avatar),
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = stringResource(R.string.my_rank), style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "#${user.rank}  ${user.name}  ${user.score} pt",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
