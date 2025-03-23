package com.example.cyberopoli.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyberopoli.ui.composables.AppBar
import com.example.cyberopoli.ui.composables.auth.Text3D

@Composable
fun AuthScreen() {
    Scaffold(
        topBar = { AppBar(title = "CYBEROPOLI", false) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text3D(
                text = "CYBEROPOLI",
                fontSize = 50,
                fontWeight = FontWeight.ExtraBold,
                textColor = Color.White,
                shadowColor = Color(0xFF0D47A1),
                offsetX = 4,
                offsetY = 4
            )
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { /* TODO*/ }) {
                        Text("ACCEDI")
                    }
                    Button(onClick = { /* TODO */ }) {
                        Text("REGISTRATI")
                    }
                    Button(onClick = { /* TODO */ }) {
                        Text("OSPITE")
                    }
                }
            }
        }
    }
}