package com.unibo.cyberopoli.ui.composables.auth

import android.icu.lang.UCharacter.toUpperCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R

@Composable
fun AuthHeader() {
    val imageSize = 250.dp

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(imageSize)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            Text3D(
                text = toUpperCase(stringResource(R.string.app_name)),
                fontSize = 50,
                fontWeight = FontWeight.ExtraBold,
                textColor = MaterialTheme.colorScheme.primary,
                shadowColor = MaterialTheme.colorScheme.onBackground,
                offsetX = 4,
                offsetY = 4,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = imageSize / 2)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
