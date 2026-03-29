package com.koupa.barberbooking.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Koupa Color System - Centralized color definitions
 * Use these colors directly or via MaterialTheme.colorScheme
 */
object KoupaColors {
    // Primary Brand Colors
    val Teal = Color(0xFF1A7A78)
    val TealLight = Color(0xFF2B9F9C)
    val TealDark = Color(0xFF004D4B)
    val TealContainer = Color(0xFFB2DFDB)
    
    // Secondary Brand Colors
    val Gold = Color(0xFFE1A553)
    val GoldLight = Color(0xFFFFF8E1)
    val GoldDark = Color(0xFFB8860B)
    val GoldContainer = Color(0xFFFFE0B2)
    
    // Neutral Colors
    val DarkSlate = Color(0xFF323E4B)
    val DarkSlateLight = Color(0xFF4A5568)
    val Gray = Color(0xFF9E9E9E)
    val LightGray = Color(0xFFE0E0E0)
    
    // Background Colors
    val Background = Color(0xFFF3F5F7)
    val BackgroundDark = Color(0xFF0F1217)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceDark = Color(0xFF1E1E1E)
    
    // Semantic Colors
    val Success = Color(0xFF2E7D32)
    val SuccessContainer = Color(0xFFE8F5E9)
    val Error = Color(0xFFC62828)
    val ErrorContainer = Color(0xFFFFEBEE)
    val Warning = Color(0xFFF57C00)
    val WarningContainer = Color(0xFFFFF3E0)
    val Info = Color(0xFF1976D2)
    val InfoContainer = Color(0xFFE3F2FD)
    
    // Text Colors
    val OnPrimary = Color.White
    val OnSecondary = Color.White
    val OnBackground = DarkSlate
    val OnBackgroundDark = Color.White
    val OnSurface = DarkSlate
    val OnSurfaceDark = Color.White
    
    // Special Gradients
    val TealGradientStart = Teal
    val TealGradientEnd = TealLight
}

// Aliases for backward compatibility
val KoupaTeal = KoupaColors.Teal
val KoupaTealLight = KoupaColors.TealLight
val KoupaTealDark = KoupaColors.TealDark
val KoupaGold = KoupaColors.Gold
val KoupaGoldLight = KoupaColors.GoldLight
val KoupaGoldDark = KoupaColors.GoldDark
val KoupaDarkSlate = KoupaColors.DarkSlate
val KoupaBackground = KoupaColors.Background
val KoupaSuccess = KoupaColors.Success
val KoupaError = KoupaColors.Error
val KoupaWarning = KoupaColors.Warning

// Gradient definitions (static brushes for use in composables)
val TealGradient = Brush.verticalGradient(
    colors = listOf(KoupaColors.Teal, KoupaColors.TealLight)
)
val GoldGradient = Brush.verticalGradient(
    colors = listOf(KoupaColors.Gold, KoupaColors.GoldLight)
)
