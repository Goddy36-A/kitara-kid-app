package com.kitarakid.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KitaraColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Charcoal,
    secondary = Teal,
    onSecondary = Cream,
    background = Charcoal,
    onBackground = Cream,
    surface = CharcoalSurface,
    onSurface = Cream,
    surfaceVariant = CharcoalSurfaceRaised,
    onSurfaceVariant = CreamDim,
    error = Error
)

@Composable
fun KitaraKidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KitaraColorScheme,
        typography = KitaraTypography,
        content = content
    )
}
