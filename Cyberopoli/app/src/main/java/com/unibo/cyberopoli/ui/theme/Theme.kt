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
    inverseOnSurface = CyberDark_OnSurface,

    outline = CyberDark_Outline,
    outlineVariant = CyberDark_OutlineVariant,

    scrim = CyberDark_Scrim,
    inversePrimary = CyberDark_InversePrimary,

    surfaceDim = CyberDark_SurfaceDim,
    surfaceBright = CyberDark_SurfaceBright,
    surfaceContainerLowest = CyberDark_SurfaceContainerLowest,
    surfaceContainerLow = CyberDark_SurfaceContainerLow,
    surfaceContainer = CyberDark_SurfaceContainer,
    surfaceContainerHigh = CyberDark_SurfaceContainerHigh,
    surfaceContainerHighest = CyberDark_SurfaceContainerHighest,
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

    surfaceDim = CyberLight_SurfaceDim,
    surfaceBright = CyberLight_SurfaceBright,
    surfaceContainerLowest = CyberLight_SurfaceContainerLowest,
    surfaceContainerLow = CyberLight_SurfaceContainerLow,
    surfaceContainer = CyberLight_SurfaceContainer,
    surfaceContainerHigh = CyberLight_SurfaceContainerHigh,
    surfaceContainerHighest = CyberLight_SurfaceContainerHighest,
)

private val DarkMediumContrastColorScheme = darkColorScheme(
    primary = CyberDarkMediumContrast_Primary,
    onPrimary = CyberDarkMediumContrast_OnPrimary,
    primaryContainer = CyberDarkMediumContrast_PrimaryContainer,
    onPrimaryContainer = CyberDarkMediumContrast_OnPrimaryContainer,
    secondary = CyberDarkMediumContrast_Secondary,
    onSecondary = CyberDarkMediumContrast_OnSecondary,
    secondaryContainer = CyberDarkMediumContrast_SecondaryContainer,
    onSecondaryContainer = CyberDarkMediumContrast_OnSecondaryContainer,
    tertiary = CyberDarkMediumContrast_Tertiary,
    onTertiary = CyberDarkMediumContrast_OnTertiary,
    tertiaryContainer = CyberDarkMediumContrast_TertiaryContainer,
    onTertiaryContainer = CyberDarkMediumContrast_OnTertiaryContainer,
    error = CyberDarkMediumContrast_Error,
    onError = CyberDarkMediumContrast_OnError,
    errorContainer = CyberDarkMediumContrast_ErrorContainer,
    onErrorContainer = CyberDarkMediumContrast_OnErrorContainer,
    background = CyberDarkMediumContrast_Background,
    onBackground = CyberDarkMediumContrast_OnBackground,
    surface = CyberDarkMediumContrast_Surface,
    onSurface = CyberDarkMediumContrast_OnSurface,
    surfaceVariant = CyberDarkMediumContrast_SurfaceVariant,
    onSurfaceVariant = CyberDarkMediumContrast_OnSurfaceVariant,
    outline = CyberDarkMediumContrast_Outline,
    outlineVariant = CyberDarkMediumContrast_OutlineVariant,
    scrim = CyberDarkMediumContrast_Scrim,
    inverseSurface = CyberDarkMediumContrast_InverseSurface,
    inverseOnSurface = CyberDarkMediumContrast_InverseOnSurface,
    inversePrimary = CyberDarkMediumContrast_InversePrimary,
    surfaceDim = CyberDarkMediumContrast_SurfaceDim,
    surfaceBright = CyberDarkMediumContrast_SurfaceBright,
    surfaceContainerLowest = CyberDarkMediumC_SurfaceContainerLowest,
    surfaceContainerLow = CyberDarkMediumC_SurfaceContainerLow,
    surfaceContainer = CyberDarkMediumC_SurfaceContainer,
    surfaceContainerHigh = CyberDarkMediumC_SurfaceContainerHigh,
    surfaceContainerHighest = CyberDarkMediumC_SurfaceContainerHighest,
)

private val DarkHighContrastColorScheme = darkColorScheme(
    primary = CyberDarkHighContrast_Primary,
    onPrimary = CyberDarkHighContrast_OnPrimary,
    primaryContainer = CyberDarkHighContrast_PrimaryContainer,
    onPrimaryContainer = CyberDarkHighContrast_OnPrimaryContainer,
    secondary = CyberDarkHighContrast_Secondary,
    onSecondary = CyberDarkHighContrast_OnSecondary,
    secondaryContainer = CyberDarkHighContrast_SecondaryContainer,
    onSecondaryContainer = CyberDarkHighContrast_OnSecondaryContainer,
    tertiary = CyberDarkHighContrast_Tertiary,
    onTertiary = CyberDarkHighContrast_OnTertiary,
    tertiaryContainer = CyberDarkHighContrast_TertiaryContainer,
    onTertiaryContainer = CyberDarkHighContrast_OnTertiaryContainer,
    error = CyberDarkHighContrast_Error,
    onError = CyberDarkHighContrast_OnError,
    errorContainer = CyberDarkHighContrast_ErrorContainer,
    onErrorContainer = CyberDarkHighContrast_OnErrorContainer,
    background = CyberDarkHighContrast_Background,
    onBackground = CyberDarkHighContrast_OnBackground,
    surface = CyberDarkHighContrast_Surface,
    onSurface = CyberDarkHighContrast_OnSurface,
    surfaceVariant = CyberDarkHighContrast_SurfaceVariant,
    onSurfaceVariant = CyberDarkHighContrast_OnSurfaceVariant,
    outline = CyberDarkHighContrast_Outline,
    outlineVariant = CyberDarkHighContrast_OutlineVariant,
    scrim = CyberDarkHighContrast_Scrim,
    inverseSurface = CyberDarkHighContrast_InverseSurface,
    inverseOnSurface = CyberDarkHighContrast_InverseOnSurface,
    inversePrimary = CyberDarkHighContrast_InversePrimary,
    surfaceDim = CyberDarkHighContrast_SurfaceDim,
    surfaceBright = CyberDarkHighContrast_SurfaceBright,
    surfaceContainerLowest = CyberDarkHighContrast_SurfaceContainerLowest,
    surfaceContainerLow = CyberDarkHighContrast_SurfaceContainerLow,
    surfaceContainer = CyberDarkHighContrast_SurfaceContainer,
    surfaceContainerHigh = CyberDarkHighContrast_SurfaceContainerHigh,
    surfaceContainerHighest = CyberDarkHighContrast_SurfaceContainerHighest,
)

private val LightMediumContrastColorScheme = lightColorScheme(
    primary = CyberLightMediumContrast_Primary,
    onPrimary = CyberLightMediumContrast_OnPrimary,
    primaryContainer = CyberLightMediumContrast_PrimaryContainer,
    onPrimaryContainer = CyberLightMediumContrast_OnPrimaryContainer,
    secondary = CyberLightMediumContrast_Secondary,
    onSecondary = CyberLightMediumContrast_OnSecondary,
    secondaryContainer = CyberLightMediumContrast_SecondaryContainer,
    onSecondaryContainer = CyberLightMediumContrast_OnSecondaryContainer,
    tertiary = CyberLightMediumContrast_Tertiary,
    onTertiary = CyberLightMediumContrast_OnTertiary,
    tertiaryContainer = CyberLightMediumContrast_TertiaryContainer,
    onTertiaryContainer = CyberLightMediumContrast_OnTertiaryContainer,
    error = CyberLightMediumContrast_Error,
    onError = CyberLightMediumContrast_OnError,
    errorContainer = CyberLightMediumContrast_ErrorContainer,
    onErrorContainer = CyberLightMediumContrast_OnErrorContainer,
    background = CyberLightMediumContrast_Background,
    onBackground = CyberLightMediumContrast_OnBackground,
    surface = CyberLightMediumContrast_Surface,
    onSurface = CyberLightMediumContrast_OnSurface,
    surfaceVariant = CyberLightMediumContrast_SurfaceVariant,
    onSurfaceVariant = CyberLightMediumContrast_OnSurfaceVariant,
    outline = CyberLightMediumContrast_Outline,
    outlineVariant = CyberLightMediumContrast_OutlineVariant,
    scrim = CyberLightMediumContrast_Scrim,
    inverseSurface = CyberLightMediumContrast_InverseSurface,
    inverseOnSurface = CyberLightMediumContrast_InverseOnSurface,
    inversePrimary = CyberLightMediumContrast_InversePrimary,
    surfaceDim = CyberLightMediumContrast_SurfaceDim,
    surfaceBright = CyberLightMediumContrast_SurfaceBright,
    surfaceContainerLowest = CyberLightMediumC_SurfaceContainerLowest,
    surfaceContainerLow = CyberLightMediumC_SurfaceContainerLow,
    surfaceContainer = CyberLightMediumC_SurfaceContainer,
    surfaceContainerHigh = CyberLightMediumC_SurfaceContainerHigh,
    surfaceContainerHighest = CyberLightMediumC_SurfaceContainerHighest,
)

private val LightHighContrastColorScheme = lightColorScheme(
    primary = CyberLightHighContrast_Primary,
    onPrimary = CyberLightHighContrast_OnPrimary,
    primaryContainer = CyberLightHighContrast_PrimaryContainer,
    onPrimaryContainer = CyberLightHighContrast_OnPrimaryContainer,
    secondary = CyberLightHighContrast_Secondary,
    onSecondary = CyberLightHighContrast_OnSecondary,
    secondaryContainer = CyberLightHighContrast_SecondaryContainer,
    onSecondaryContainer = CyberLightHighContrast_OnSecondaryContainer,
    tertiary = CyberLightHighContrast_Tertiary,
    onTertiary = CyberLightHighContrast_OnTertiary,
    tertiaryContainer = CyberLightHighContrast_TertiaryContainer,
    error = CyberLightHighContrast_Error,
    onError = CyberLightHighContrast_OnError,
    errorContainer = CyberLightHighContrast_ErrorContainer,
    onErrorContainer = CyberLightHighContrast_OnErrorContainer,
    background = CyberLightHighContrast_Background,
    onBackground = CyberLightHighContrast_OnBackground,
    surface = CyberLightHighContrast_Surface,
    onSurface = CyberLightHighContrast_OnSurface,
    surfaceVariant = CyberLightHighContrast_SurfaceVariant,
    onSurfaceVariant = CyberLightHighContrast_OnSurfaceVariant,
    outline = CyberLightHighContrast_Outline,
    outlineVariant = CyberLightHighContrast_OutlineVariant,
    scrim = CyberLightHighContrast_Scrim,
    inverseSurface = CyberLightHighContrast_InverseSurface,
    inverseOnSurface = CyberLightHighContrast_InverseOnSurface,
    inversePrimary = CyberLightHighContrast_InversePrimary,
    surfaceDim = CyberLightHighContrast_SurfaceDim,
    surfaceBright = CyberLightHighContrast_SurfaceBright,
    surfaceContainerLowest = CyberLightHighContrast_SurfaceContainerLowest,
    surfaceContainerLow = CyberLightHighContrast_SurfaceContainerLow,
    surfaceContainer = CyberLightHighContrast_SurfaceContainer,
    surfaceContainerHigh = CyberLightHighContrast_SurfaceContainerHigh,
    surfaceContainerHighest = CyberLightHighContrast_SurfaceContainerHighest,
)

enum class ContrastLevel {
    BASE, MEDIUM, HIGH
}

@Composable
fun CyberopoliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    contrastLevel: ContrastLevel = ContrastLevel.BASE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> {
            when (contrastLevel) {
                ContrastLevel.MEDIUM -> DarkMediumContrastColorScheme
                ContrastLevel.HIGH -> DarkHighContrastColorScheme
                else -> DarkColorScheme
            }
        }
        else -> {
            when (contrastLevel) {
                ContrastLevel.MEDIUM -> LightMediumContrastColorScheme
                ContrastLevel.HIGH -> LightHighContrastColorScheme
                else -> LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}