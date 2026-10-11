package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ToxicColorScheme = darkColorScheme(
    primary = ToxicEmerald,
    onPrimary = ToxicBackground,
    primaryContainer = ToxicEmeraldDark,
    onPrimaryContainer = ToxicEmeraldBright,
    secondary = ToxicEmeraldBright,
    onSecondary = ToxicBackground,
    secondaryContainer = ToxicSurfaceHigh,
    onSecondaryContainer = ToxicTextPrimary,
    tertiary = ToxicCyan,
    onTertiary = ToxicBackground,
    tertiaryContainer = ToxicSurfaceVariant,
    onTertiaryContainer = ToxicCyan,
    background = ToxicBackground,
    onBackground = ToxicTextPrimary,
    surface = ToxicSurface,
    onSurface = ToxicTextPrimary,
    surfaceVariant = ToxicSurfaceVariant,
    onSurfaceVariant = ToxicTextSecondary,
    outline = ToxicBorder,
    outlineVariant = ToxicBorderLight,
    error = ToxicAccentRed,
    onError = ToxicTextPrimary,
    errorContainer = ToxicRedDark,
    onErrorContainer = ToxicRedBright
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark developer theme
    content: @Composable () -> Unit
) {
    val colorScheme = ToxicColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ToxicBackground.toArgb()
                window.navigationBarColor = ToxicBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
