package com.unibo.cyberopoli.ui.screens.auth.view.composables

import com.unibo.cyberopoli.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.google_sign_in),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}
