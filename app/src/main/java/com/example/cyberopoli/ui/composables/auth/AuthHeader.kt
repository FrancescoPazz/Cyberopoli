package com.example.cyberopoli.ui.composables.auth

import android.icu.lang.UCharacter.toUpperCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberopoli.R

@Composable
fun AuthHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )
        }

        Text3D(
            text = toUpperCase(stringResource(R.string.app_name)),
            fontSize = 50,
            fontWeight = FontWeight.ExtraBold,
            textColor = MaterialTheme.colorScheme.surface,
            shadowColor = MaterialTheme.colorScheme.secondary,
            offsetX = 4,
            offsetY = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}
