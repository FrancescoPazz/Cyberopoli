package com.unibo.cyberopoli.ui.composables.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.UserData

@Composable
fun ProfileHeader(
    userData: UserData, onEditProfileClick: () -> Unit, onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = userData.profileImageUrl?.let { rememberAsyncImagePainter(it) } ?: painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.avatar),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        userData.name?.let { name ->
            userData.surname?.let { surname ->
                Text(
                    text = "$name $surname",
                )
            }
        }

        Text(
            text = "${stringResource(R.string.level)}: ${userData.level.toString()}", textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            ProfileButton(
                text = stringResource(R.string.edit), icon = {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.edit),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }, onClick = onEditProfileClick
            )
            ProfileButton(
                text = stringResource(R.string.share), icon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }, onClick = onShareClick
            )

        }
    }
}
