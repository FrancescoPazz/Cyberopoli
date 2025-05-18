package com.unibo.cyberopoli.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyberDark_Primary,
    onPrimary = CyberDark_OnPrimary,
    primaryContainer = CyberDark_PrimaryContainer,
    onPrimaryContainer = CyberDark_OnPrimaryContainer,

    secondary = CyberDark_Secondary,
    onSecondary = CyberDark_OnSecondary,
    secondaryContainer = CyberDark_SecondaryContainer,
    onSecondaryContainer = CyberDark_OnSecondaryContainer,

    tertiary = CyberDark_Tertiary,
    onTertiary = CyberDark_OnTertiary,
    tertiaryContainer = CyberDark_TertiaryContainer,
    onTertiaryContainer = CyberDark_OnTertiaryContainer,

    error = CyberDark_Error,
    onError = CyberDark_OnError,
    errorContainer = CyberDark_ErrorContainer,
    onErrorContainer = CyberDark_OnErrorContainer,

    background = CyberDark_Background,
    onBackground = CyberDark_OnBackground,

    surface = CyberDark_Surface,
    onSurface = CyberDark_OnSurface,
    surfaceVariant = CyberDark_SurfaceVariant,
    onSurfaceVariant = CyberDark_OnSurfaceVariant,
    inverseSurface = CyberDark_InverseSurface,
    inverseOnSurface = CyberDark_InverseOnSurface,

    outline = CyberDark_Outline,
    outlineVariant = CyberDark_OutlineVariant,

    scrim = CyberDark_Scrim,
    inversePrimary = CyberDark_InversePrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = CyberLight_Primary,
    onPrimary = CyberLight_OnPrimary,
    primaryContainer = CyberLight_PrimaryContainer,
    onPrimaryContainer = CyberLight_OnPrimaryContainer,

    secondary = CyberLight_Secondary,
    onSecondary = CyberLight_OnSecondary,
    secondaryContainer = CyberLight_SecondaryContainer,
    onSecondaryContainer = CyberLight_OnSecondaryContainer,

    tertiary = CyberLight_Tertiary,
    onTertiary = CyberLight_OnTertiary,
    tertiaryContainer = CyberLight_TertiaryContainer,
    onTertiaryContainer = CyberLight_OnTertiaryContainer,

    error = CyberLight_Error,
    onError = CyberLight_OnError,
    errorContainer = CyberLight_ErrorContainer,
    onErrorContainer = CyberLight_OnErrorContainer,

    background = CyberLight_Background,
    onBackground = CyberLight_OnBackground,

    surface = CyberLight_Surface,
    onSurface = CyberLight_OnSurface,
    surfaceVariant = CyberLight_SurfaceVariant,
    onSurfaceVariant = CyberLight_OnSurfaceVariant,
    inverseSurface = CyberLight_InverseSurface,
    inverseOnSurface = CyberLight_InverseOnSurface,

    outline = CyberLight_Outline,
    outlineVariant = CyberLight_OutlineVariant,

    scrim = CyberLight_Scrim,
    inversePrimary = CyberLight_InversePrimary,
)

@Composable
fun CyberopoliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}