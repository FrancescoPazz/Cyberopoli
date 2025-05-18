package com.unibo.cyberopoli.ui.screens.auth.composables

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
fun AuthOutlinedTextField(
    value: MutableState<String>,
    placeholder: String,
    singleLine: Boolean = false,
    imageVector: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
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
            .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary, // Testo digitato focused
            unfocusedTextColor = MaterialTheme.colorScheme.primary, // Testo digitato unfocused
            focusedBorderColor = MaterialTheme.colorScheme.primary, // Bordo focused
            unfocusedBorderColor = MaterialTheme.colorScheme.outline, // *** MODIFICATO: Usa outline ***
            errorBorderColor = MaterialTheme.colorScheme.error, // Bordo in stato di errore
            cursorColor = MaterialTheme.colorScheme.primary, // Cursore focused
            errorCursorColor = MaterialTheme.colorScheme.error, // Cursore in stato di errore
            focusedLabelColor = MaterialTheme.colorScheme.primary, // Label/Placeholder focused
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, // *** MODIFICATO: Usa onSurfaceVariant ***
            errorLabelColor = MaterialTheme.colorScheme.error, // Label/Placeholder in stato di errore
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary, // Icona leading focused
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // *** MODIFICATO: Usa onSurfaceVariant ***
            errorLeadingIconColor = MaterialTheme.colorScheme.error, // Icona leading in stato di errore
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f), // Colore testo disabilitato standard
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f), // Colore bordo disabilitato standard
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f), // Colore icona disabilitata standard
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f) // Colore placeholder disabilitato standard
        )
    )
}