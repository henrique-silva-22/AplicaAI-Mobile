package br.com.aplicaai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AplicaAiColorScheme = darkColorScheme(
    primary = Color(0xFF2878FF),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFF0B1426),
    onBackground = Color(0xFFF5F7FA),
    surface = Color(0xFF1E2A3B),
    onSurface = Color(0xFFF5F7FA),
    surfaceVariant = Color(0xFF39475B),
    onSurfaceVariant = Color(0xFFAAB8CE),
    secondary = Color(0xFFAAB8CE),
    onSecondary = Color(0xFF0B1426),
    tertiary = Color(0xFF35C878),
    onTertiary = Color(0xFFFFFFFF),
    error = Color(0xFFFF5B62),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun AplicaAiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AplicaAiColorScheme,
        content = content
    )
}