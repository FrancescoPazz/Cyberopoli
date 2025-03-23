package com.example.cyberopoli.ui.composables

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Medium
            )
        },
    )
}