package com.koupa.barberbooking.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Color tokens are defined in Color.kt - use KoupaColors or the aliases there

// ── Light Color Scheme ────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary             = KoupaTeal,
    onPrimary           = Color.White,
    primaryContainer    = Color(0xFFB2DFDB),
    onPrimaryContainer  = KoupaDarkSlate,
    secondary           = KoupaGold,
    onSecondary         = Color.White,
    secondaryContainer  = Color(0xFFFFE0B2),
    onSecondaryContainer= KoupaDarkSlate,
    tertiary            = KoupaDarkSlate,
    onTertiary          = Color.White,
    background          = KoupaBackground,
    onBackground        = KoupaDarkSlate,
    surface             = Color.White,
    onSurface           = KoupaDarkSlate,
    surfaceVariant      = Color(0xFFF5F5F5),
    onSurfaceVariant    = Color(0xFF6B7280),
    outlineVariant      = Color(0xFFE8ECF0),
    error               = KoupaError,
    onError             = Color.White
)

// ── Dark Color Scheme ─────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary             = KoupaTealLight,
    onPrimary           = Color.White,
    primaryContainer    = Color(0xFF004D4B),
    onPrimaryContainer  = Color(0xFFB2DFDB),
    secondary           = KoupaGold,
    onSecondary         = Color(0xFF1A1A1A),
    secondaryContainer  = Color(0xFF4A3500),
    onSecondaryContainer= Color(0xFFFFE0B2),
    tertiary            = Color(0xFF9EAAB8),
    onTertiary          = Color.Black,
    background          = Color(0xFF0F1217),   // very deep navy
    onBackground        = Color(0xFFE8ECF0),
    surface             = Color(0xFF1A1F27),   // dark card
    onSurface           = Color(0xFFE8ECF0),
    surfaceVariant      = Color(0xFF242B35),
    onSurfaceVariant    = Color(0xFF9EAAB8),
    outlineVariant      = Color(0xFF2C3440),
    error               = Color(0xFFEF9A9A),
    onError             = Color(0xFF690000)
)

// ── Theme Composable ──────────────────────────────────────────────────────────
@Composable
fun KoupaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled so our brand palette is always used
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography   = KoupaTypography,
        content      = content
    )
}
