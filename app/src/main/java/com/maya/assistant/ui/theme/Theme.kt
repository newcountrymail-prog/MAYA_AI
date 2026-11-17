package com.maya.assistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MayaNavy,
    onPrimary = Color.White,
    secondary = MayaCyan,
    onSecondary = Color.White,
    background = MayaBgLight,
    onBackground = MayaTextPrimary,
    surface = MayaSurfaceLight,
    onSurface = MayaTextPrimary,
    surfaceVariant = MayaCardLight,
    onSurfaceVariant = MayaTextSecondary,
    outline = MayaShadowDark
)

private val DarkColorScheme = darkColorScheme(
    primary = MayaCyanAccent,
    onPrimary = MayaNavy,
    secondary = MayaPurpleAccent,
    onSecondary = Color.White,
    background = MayaBgDark,
    onBackground = Color.White,
    surface = MayaSurfaceDark,
    onSurface = Color.White,
    surfaceVariant = MayaCardDark,
    onSurfaceVariant = MayaTextMuted,
    outline = MayaDarkShadowDark
)

@Composable
fun MayaAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
