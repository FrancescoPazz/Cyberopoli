package com.unibo.cyberopoli.ui.screens.settings.view.composables

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.ui.components.CyberOutlinedTextField

@Composable
fun ChangePasswordSection(
    updatePasswordWithOldPassword: (oldPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.change_password),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = currentPassword,
            imageVector = Icons.Default.Lock,
            placeholder = stringResource(R.string.old_password),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = newPassword,
            imageVector = Icons.Default.Lock,
            placeholder = stringResource(R.string.password),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CyberOutlinedTextField(
            value = confirmPassword,
            imageVector = Icons.Default.Lock,
            placeholder = stringResource(R.string.password_confirm),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (newPassword == confirmPassword) {
                    updatePasswordWithOldPassword(
                        currentPassword.value,
                        newPassword.value,
                        {
                            Toast.makeText(
                                context,
                                context.getString(R.string.change_success),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                        {
                            Toast.makeText(
                                context,
                                context.getString(R.string.change_fail),
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.password_not_match),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
            colors =
                ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    containerColor = MaterialTheme.colorScheme.onTertiary,
                ),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(stringResource(R.string.change_password))
        }
    }
}
