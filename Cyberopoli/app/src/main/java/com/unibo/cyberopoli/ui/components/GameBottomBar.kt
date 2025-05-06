package com.unibo.cyberopoli.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.Phase

@Composable
fun GameBottomBar(
    phase: Phase,
    diceRoll: Int?,
    onRoll: () -> Unit,
    onMove: (Int) -> Unit,
    onEndTurn: () -> Unit,
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xFF27293D),
        tonalElevation = 8.dp
    ) {
        when(phase) {
            Phase.ROLL_DICE -> {
                NavigationBarItem(
                    icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_dice),
                                contentDescription = stringResource(R.string.roll_dice),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.roll_dice))
                        }
                    },
                    selected = false,
                    onClick = onRoll
                )
            }
            Phase.MOVE -> {
                NavigationBarItem(
                    icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_move_on),
                                contentDescription = "${stringResource(R.string.move_on)} $diceRoll ${stringResource(R.string.cells)}",
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${stringResource(R.string.move_on)} $diceRoll ${stringResource(R.string.cells)}")
                        }
                    },
                    selected = false,
                    onClick = { onMove(diceRoll ?: 0) }
                )
            }
            Phase.CHANCE -> {
                NavigationBarItem(
                    icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_chance),
                                contentDescription = stringResource(R.string.chance),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.chance))
                        }

                    },
                    selected = false,
                    onClick = { }
                )
            }
            Phase.HACKER -> {
                NavigationBarItem(
                    icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_hacker),
                                contentDescription = stringResource(R.string.hacker),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.hacker))
                        }
                    },
                    selected = false,
                    onClick = { }
                )
            }
            Phase.END_TURN -> {
                NavigationBarItem(
                    icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_skip),
                                contentDescription = stringResource(R.string.turn_pass),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.turn_pass))
                        }
                    },
                    selected = false,
                    onClick = onEndTurn
                )
            }
            else -> {
                NavigationBarItem(
                    icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_stop_hand),
                                contentDescription = stringResource(R.string.wait_your_turn),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.wait_your_turn))
                        }
                    },
                    selected = false,
                    onClick = { }
                )
            }
        }
    }
}

