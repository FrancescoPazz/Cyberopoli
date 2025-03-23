package com.example.cyberopoli.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, canComeBack: Boolean = true) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Medium
            )
        },
        actions = {
            if (canComeBack) {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}