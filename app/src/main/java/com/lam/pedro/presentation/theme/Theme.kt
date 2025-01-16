package com.lam.pedro.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette: ColorScheme = darkColorScheme(
    primary = PedroYellow,
    secondary = PedroBlack,
    primaryContainer = PedroDarkGray,
    secondaryContainer = PedroDark,
    onSecondaryContainer = PedroLighterGray,
    onPrimary = PedroLighterYellow,
    surfaceContainer = PedroBottomBar
)

private val LightColorPalette: ColorScheme = lightColorScheme(
    primary = PedroBlack,
    secondary = PedroBlack,
    primaryContainer = PedroLightGray,
    secondaryContainer = PedroLight,
    onSecondaryContainer = PedroLightGray
)

@Composable
fun PedroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}