package com.unibo.cyberopoli.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CyberOutlinedTextField(
    value: MutableState<String>,
    placeholder: String,
    singleLine: Boolean = false,
    imageVector: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
        label = { Text(placeholder) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            imageVector?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
    )
}
