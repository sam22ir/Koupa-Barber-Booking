package com.koupa.barberbooking.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Koupa Design Tokens
val KoupaTeal = Color(0xFF1A7A78)
val KoupaGold = Color(0xFFE1A553)
val KoupaDarkSlate = Color(0xFF323E4B)
val KoupaBackground = Color(0xFFF3F5F7)
val KoupaSuccess = Color(0xFF2E7D32)
val KoupaError = Color(0xFFC62828)

private val LightColorScheme = lightColorScheme(
    primary = KoupaTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = KoupaDarkSlate,
    secondary = KoupaGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = KoupaDarkSlate,
    tertiary = KoupaDarkSlate,
    onTertiary = Color.White,
    background = KoupaBackground,
    onBackground = KoupaDarkSlate,
    surface = Color.White,
    onSurface = KoupaDarkSlate,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = KoupaDarkSlate,
    error = KoupaError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = KoupaTeal,
    onPrimary = Color.White,
    secondary = KoupaGold,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = KoupaError,
    onError = Color.White
)

@Composable
fun KoupaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KoupaTypography,
        content = content
    )
}
