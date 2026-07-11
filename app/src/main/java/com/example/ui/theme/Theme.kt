package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkSaffronPrimary,
    secondary = DarkSaffronSecondary,
    tertiary = DarkSaffronTertiary,
    background = DarkCreamBackground,
    surface = DarkCreamSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFFF3ECE5),
    onSurface = Color(0xFFF3ECE5)
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    secondary = SaffronSecondary,
    tertiary = SaffronTertiary,
    background = CreamBackground,
    surface = CreamSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFF332010),
    onSurface = Color(0xFF332010)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to enforce our hand-crafted brand colors
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
