package com.example.cyberopoli.ui.composables.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthOutlinedTextField(
    value: MutableState<String>,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF333333),
            unfocusedTextColor = Color(0xFF0000FF),
            focusedContainerColor = Color(0xFFEFEFEF),
            unfocusedContainerColor = Color(0xFFEFEFEF),
            focusedBorderColor = Color(0xFF0000FF),
            unfocusedBorderColor = Color(0xFF000080),
            focusedPlaceholderColor = Color(0xFF888888),
        )
    )
}