package com.example.cyberopoli.ui.composables.auth

import android.icu.lang.UCharacter.toUpperCase
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.R
import com.example.cyberopoli.ui.composables.AppBar
import com.example.cyberopoli.ui.theme.CyberopoliTheme

@Composable
fun AuthTemplate(
    navController: NavController,
    cardContent: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = { AppBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text3D(
                text = toUpperCase(stringResource(R.string.app_name)),
                fontSize = 50,
                fontWeight = FontWeight.ExtraBold,
                textColor = MaterialTheme.colorScheme.surface,
                shadowColor = MaterialTheme.colorScheme.secondary,
                offsetX = 4,
                offsetY = 4
            )

            Spacer(modifier = Modifier.height(36.dp))

            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = cardContent
                )
            }
        }
    }
}
