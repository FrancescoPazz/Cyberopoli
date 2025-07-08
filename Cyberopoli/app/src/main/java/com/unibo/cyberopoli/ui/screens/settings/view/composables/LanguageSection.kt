package com.unibo.cyberopoli.ui.screens.settings.view.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R

@Composable
fun LanguageSection(
    onShowLanguageDialog: () -> Unit,
    selectedLanguage: String,
    onSelect: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.choose_language),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Row {
        listOf("it" to stringResource(R.string.italian), "en" to stringResource(R.string.english))
            .forEach { (code, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = (selectedLanguage == code),
                        onClick = {
                            onSelect(code)
                            onShowLanguageDialog()
                        }
                    )
                    Text(text = label)
                }
            }
    }
}
