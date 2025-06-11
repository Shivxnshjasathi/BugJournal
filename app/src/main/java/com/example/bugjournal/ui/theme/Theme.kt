package com.example.bugjournal.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DefaultColorScheme = lightColorScheme(
    primary = Color.White,
    secondary = Color.White,
    tertiary = Color.White,
    background = Color.White,
    surface = Color.White
)

@Composable
fun BugJournalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DefaultColorScheme,
        typography = AppTypography,
        content = content
    )
}
