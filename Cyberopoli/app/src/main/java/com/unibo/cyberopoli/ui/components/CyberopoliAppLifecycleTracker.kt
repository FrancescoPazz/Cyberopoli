package com.unibo.cyberopoli.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner

enum class AppLifecycleTrackerScreenContext {
    GAME, LOBBY,
}

private const val TIME_OUT_SECONDS = 30

@Composable
fun AppLifecycleTracker(
    context: AppLifecycleTrackerScreenContext,
    setInApp: (Boolean) -> Unit,
    onTimedOut: () -> Unit,
) {
    var backgroundTimestamp by remember { mutableLongStateOf(0L) }
    var hasBeenInBackground by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        setInApp(true)

        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    backgroundTimestamp = System.currentTimeMillis()
                    hasBeenInBackground = true
                    if (context == AppLifecycleTrackerScreenContext.GAME) {
                        Log.d("AppLifecycle IN-GAME", "App moved to BACKGROUND (ON_STOP)")
                    } else if (context == AppLifecycleTrackerScreenContext.LOBBY) {
                        Log.d("AppLifecycle IN-LOBBY", "App moved to BACKGROUND (ON_STOP)")
                    }
                    setInApp(false)
                }

                Lifecycle.Event.ON_START -> {
                    if (hasBeenInBackground) {
                        val elapsed = (System.currentTimeMillis() - backgroundTimestamp) / 1000
                        if (elapsed > TIME_OUT_SECONDS) {
                            Log.d("AppLifecycle", "App has been in background for $elapsed seconds")
                            onTimedOut()
                        }
                        if (context == AppLifecycleTrackerScreenContext.GAME) {
                            Log.d(
                                "AppLifecycle IN-GAME",
                                "App moved to FOREGROUND (ON_START), spent $elapsed s in bg"
                            )
                        } else if (context == AppLifecycleTrackerScreenContext.LOBBY) {
                            Log.d(
                                "AppLifecycle IN-LOBBY",
                                "App moved to FOREGROUND (ON_START), spent $elapsed s in bg"
                            )
                        }
                        setInApp(true)
                    }
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}
