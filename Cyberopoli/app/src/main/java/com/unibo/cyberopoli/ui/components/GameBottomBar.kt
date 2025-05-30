package com.unibo.cyberopoli.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.data.models.game.GameAction

@SuppressLint("DiscouragedApi")
@Composable
fun GameBottomBar(actions: List<GameAction>) {
    val context = LocalContext.current

    NavigationBar {
        actions.forEach { action ->
            NavigationBarItem(
                icon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        action.iconRes?.let { painterResource(it) }?.let {
                            Icon(
                                painter = it,
                                contentDescription = null,
                            )
                        }
                        Spacer(Modifier.width(4.dp))

                        val resId =
                            remember(action.id) {
                                context.resources.getIdentifier(
                                    action.id,
                                    "string",
                                    context.packageName,
                                )
                            }
                        Text(stringResource(resId))
                    }
                },
                selected = false,
                onClick = action.action,
            )
        }
    }
}
