// Theme.kt
package com.example.bugjournal.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DefaultColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun BugJournalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DefaultColorScheme,
        typography = AppTypography,
        content = content
    )
}
