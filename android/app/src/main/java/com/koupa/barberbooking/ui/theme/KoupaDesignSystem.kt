package com.koupa.barberbooking.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════════════
// Koupa Design System — Material 3 Tokens & Reusable Components
// Enhanced for Algerian Market — RTL-First, Accessible, Polished
// ═══════════════════════════════════════════════════════════════════════════════

// ── Animation Tokens ───────────────────────────────────────────────────────────
object KoupaAnimationTokens {
    // Durations (Material 3 spec + Algerian market preference for smooth feel)
    const val DURATION_INSTANT = 100
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 500
    const val DURATION_EXTRA_LONG = 700

    // Easing curves (Material 3 standard)
    val EasingStandard = FastOutSlowInEasing
    val EasingDecelerate = LinearOutSlowInEasing
    val EasingAccelerate = FastOutLinearInEasing
    val EasingLinear = LinearEasing
    val EasingEmphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    // Spring specs
    val SpringBouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val SpringGentle = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
    val SpringSnappy = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

// Tween specs
fun tweenStandard() = tween<Float>(durationMillis = DURATION_MEDIUM, easing = EasingStandard)
fun tweenEnter() = tween<IntOffset>(durationMillis = DURATION_MEDIUM, easing = EasingDecelerate)
fun tweenExit() = tween<IntOffset>(durationMillis = DURATION_SHORT, easing = EasingAccelerate)
fun tweenEmphasized() = tween<Float>(durationMillis = DURATION_LONG, easing = EasingEmphasized)

// Enter/Exit transitions
val koupaFadeIn = androidx.compose.animation.fadeIn(animationSpec = tweenStandard())
val koupaFadeOut = androidx.compose.animation.fadeOut(animationSpec = tweenStandard())
val slideInFromBottom = slideInVertically(
initialOffsetY = { it / 4 },
animationSpec = tweenEnter()
) + koupaFadeIn
val slideOutToBottom = slideOutVertically(
targetOffsetY = { it / 4 },
animationSpec = tweenExit()
) + koupaFadeOut
val scaleIn: androidx.compose.animation.EnterTransition = androidx.compose.animation.scaleIn(
initialScale = 0.8f,
animationSpec = tweenStandard()
)
val scaleOut: androidx.compose.animation.ExitTransition = androidx.compose.animation.scaleOut(
targetScale = 0.8f,
animationSpec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)
)
}

// ── Shape Tokens ───────────────────────────────────────────────────────────────
object KoupaShapes {
    val ExtraSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(24.dp)
    val Full = CircleShape

    // Component-specific shapes
    val CardShape = Large
    val ButtonShape = Medium
    val BadgeShape = RoundedCornerShape(percent = 50) // pill
    val InputShape = Medium
    val IconBoxShape = Medium
    val DialogShape = ExtraLarge
    val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val SnackbarShape = Medium
    val ChipShape = Small
    val AvatarShape = Full
    val ImageShape = Large
    val TopAppBarShape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
}

// ── Spacing Tokens ────────────────────────────────────────────────────────────
object KoupaSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp

    // Component-specific spacing
    val CardPadding = md
    val ButtonHeight = 56.dp
    val ButtonHeightCompact = 44.dp
    val InputHeight = 56.dp
    val IconSize = 24.dp
    val IconSizeSmall = 20.dp
    val IconSizeLarge = 32.dp
    val IconBoxSize = 48.dp
    val AvatarSizeSmall = 32.dp
    val AvatarSizeMedium = 40.dp
    val AvatarSizeLarge = 56.dp
    val TouchTargetMin = 48.dp // Accessibility minimum

    // Screen spacing
    val ScreenHorizontalPadding = md
    val ScreenVerticalPadding = sm
    val SectionSpacing = lg
    val ItemSpacing = sm
}

// ── Elevation Tokens (Tonal Elevation via Surface Tints) ───────────────────────
object KoupaElevation {
    // Use tonal elevation instead of shadows for Material 3
    val Level0 = 0.dp
    val Level1 = 1.dp
    val Level2 = 3.dp
    val Level3 = 6.dp
    val Level4 = 8.dp
    val Level5 = 12.dp

    // Component-specific elevation
    val CardElevation = Level1
    val CardElevationElevated = Level2
    val ButtonElevation = Level0
    val ButtonElevationPressed = Level1
    val DialogElevation = Level3
    val BottomSheetElevation = Level2
    val FloatingActionButtonElevation = Level3
    val AppBarElevation = Level0
    val SnackbarElevation = Level3
}

// ── Shadow Tokens (for non-Material surfaces) ─────────────────────────────────
object KoupaShadows {
    val ShadowSm = 2.dp
    val ShadowMd = 4.dp
    val ShadowLg = 8.dp
    val ShadowXl = 16.dp
    val Shadow2xl = 24.dp
}

// ── Gradient Tokens ─────────────────────────────────────────────────────────────
object KoupaGradients {
    @Composable
    fun primaryGradient(): Brush {
        val primary = MaterialTheme.colorScheme.primary
        val primaryContainer = MaterialTheme.colorScheme.primaryContainer
        return Brush.verticalGradient(
            colors = listOf(primary, primaryContainer)
        )
    }

    @Composable
    fun primaryGradientHorizontal(): Brush {
        val primary = MaterialTheme.colorScheme.primary
        val primaryContainer = MaterialTheme.colorScheme.primaryContainer
        return Brush.horizontalGradient(
            colors = listOf(primary, primaryContainer)
        )
    }

    @Composable
    fun surfaceGradient(): Brush {
        val surface = MaterialTheme.colorScheme.surface
        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        return Brush.verticalGradient(
            colors = listOf(surface, surfaceVariant)
        )
    }

    @Composable
    fun premiumGradient(): Brush {
        val gold = KoupaExtendedColors.Gold
        val goldLight = KoupaExtendedColors.GoldLight
        return Brush.linearGradient(
            colors = listOf(gold, goldLight)
        )
    }

    @Composable
    fun successGradient(): Brush {
        val success = KoupaExtendedColors.Success
        val successLight = KoupaExtendedColors.SuccessLight
        return Brush.verticalGradient(
            colors = listOf(success, successLight)
        )
    }

    @Composable
    fun shimmerGradient(): Brush {
        val surface = MaterialTheme.colorScheme.surface
        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        return Brush.linearGradient(
            colors = listOf(surfaceVariant, surface, surfaceVariant),
            start = Offset.Zero,
            end = Offset.Infinite
        )
    }
}

// ── Extended Color Tokens (Semantic Colors for Algerian Market) ────────────────
object KoupaExtendedColors {
    // Status badges
    val BadgeConfirmed = Color(0xFFA7F3D0)
    val BadgePending = Color(0xFFFED7AA)
    val BadgeCancelled = Color(0xFFD1D5DB)

    // Brand colors
    val Brown = Color(0xFF8B5A2B)
    val LightTeal = Color(0xFFE8F5F5)
    val LightBeige = Color(0xFFFDF6E3)
    val DarkBrown = Color(0xFF8D6E14)

    // Semantic colors
    val Success = Color(0xFF2E7D32)
    val SuccessLight = Color(0xFFE8F5E9)
    val SuccessContainer = Color(0xFFB9F6CA)

    val Warning = Color(0xFFF57C00)
    val WarningLight = Color(0xFFFFF3E0)
    val WarningContainer = Color(0xFFFFE0B2)

    val Error = Color(0xFFC62828)
    val ErrorLight = Color(0xFFFFEBEE)
    val ErrorContainer = Color(0xFFFFCDD2)

    val Info = Color(0xFF1565C0)
    val InfoLight = Color(0xFFE3F2FD)
    val InfoContainer = Color(0xFFBBDEFB)

    // Premium/Gold
    val Gold = Color(0xFFE1A553)
    val GoldLight = Color(0xFFFFF8E1)
    val GoldDark = Color(0xFFB8860B)

    // Cash/Payment
    val Cash = Color(0xFF2E7D32)
    val CashContainer = Color(0xFFE8F5E9)

    // Neutral
    val Gray = Color(0xFF6B7280)
    val LightGray = Color(0xFFF3F5F7)
    val DarkGray = Color(0xFF374151)

    // Input
    val InputBorder = Color(0xFFE8ECF0)
    val InputBorderFocused = Color(0xFF1A7A78)
    val Placeholder = Color(0xFF9EAAB8)

    // Surface
    val Surface = Color(0xFFF8F9FB)
    val SurfaceContainerLow = Color(0xFFF2F4F6)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
}

// ── Badge Types (Semantic) ─────────────────────────────────────────────────────
enum class KoupaBadgeType(
    val containerColor: @Composable () -> Color,
    val contentColor: @Composable () -> Color,
    val label: String
) {
    Confirmed(
        containerColor = { KoupaExtendedColors.BadgeConfirmed },
        contentColor = { MaterialTheme.colorScheme.onSurface },
        label = "Confirmed"
    ),
    Pending(
        containerColor = { KoupaExtendedColors.BadgePending },
        contentColor = { MaterialTheme.colorScheme.onSurface },
        label = "Pending"
    ),
    Cancelled(
        containerColor = { KoupaExtendedColors.BadgeCancelled },
        contentColor = { MaterialTheme.colorScheme.onSurfaceVariant },
        label = "Cancelled"
    ),
    Success(
        containerColor = { MaterialTheme.colorScheme.primaryContainer },
        contentColor = { MaterialTheme.colorScheme.onPrimaryContainer },
        label = "Success"
    ),
    Error(
        containerColor = { MaterialTheme.colorScheme.errorContainer },
        contentColor = { MaterialTheme.colorScheme.onErrorContainer },
        label = "Error"
    ),
    Warning(
        containerColor = { KoupaExtendedColors.WarningContainer },
        contentColor = { MaterialTheme.colorScheme.onSurface },
        label = "Warning"
    ),
    Info(
        containerColor = { KoupaExtendedColors.InfoContainer },
        contentColor = { MaterialTheme.colorScheme.onSurface },
        label = "Info"
    ),
    Premium(
        containerColor = { KoupaExtendedColors.GoldLight },
        contentColor = { KoupaExtendedColors.GoldDark },
        label = "Premium"
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// Reusable Components
// ═══════════════════════════════════════════════════════════════════════════════

// ── KoupaCard (Tonal Elevation) ────────────────────────────────────────────────
@Composable
fun KoupaCard(
    modifier: Modifier = Modifier,
    tonalElevation: Dp = KoupaElevation.CardElevation,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KoupaShapes.CardShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(tonalElevation),
        content = content
    )
}

// ── KoupaElevatedCard (Higher Elevation) ───────────────────────────────────────
@Composable
fun KoupaElevatedCard(
    modifier: Modifier = Modifier,
    tonalElevation: Dp = KoupaElevation.Level2,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KoupaShapes.CardShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(tonalElevation),
        content = content
    )
}

// ── KoupaFilledCard (Surface Container) ────────────────────────────────────────
@Composable
fun KoupaFilledCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KoupaShapes.CardShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainer
        ),
        content = content
    )
}

// ── KoupaButton (Filled) ───────────────────────────────────────────────────────
@Composable
fun KoupaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.DEFAULT
) {
    val height = when (size) {
        ButtonSize.COMPACT -> KoupaSpacing.ButtonHeightCompact
        ButtonSize.DEFAULT -> KoupaSpacing.ButtonHeight
        ButtonSize.LARGE -> 64.dp
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = KoupaShapes.ButtonShape,
        enabled = enabled && !loading,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = KoupaElevation.ButtonElevation
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
                if (icon != null) {
                    Spacer(modifier = Modifier.width(KoupaSpacing.sm))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

enum class ButtonSize {
    COMPACT,
    DEFAULT,
    LARGE
}

// ── KoupaSecondaryButton (Tonal) ───────────────────────────────────────────────
@Composable
fun KoupaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(KoupaSpacing.ButtonHeight),
        shape = KoupaShapes.ButtonShape,
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(KoupaSpacing.sm))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── KoupaOutlinedButton ─────────────────────────────────────────────────────────
@Composable
fun KoupaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isDanger: Boolean = false
) {
    val borderColor = if (isDanger) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outline
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(KoupaSpacing.ButtonHeight),
        shape = KoupaShapes.ButtonShape,
        enabled = enabled,
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(KoupaSpacing.sm))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── KoupaTextButton ────────────────────────────────────────────────────────────
@Composable
fun KoupaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(KoupaSpacing.sm))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── KoupaBadge (Assist Chip style) ─────────────────────────────────────────────
@Composable
fun KoupaBadge(
    text: String,
    type: KoupaBadgeType,
    modifier: Modifier = Modifier
) {
    val containerColor = type.containerColor()
    val contentColor = type.contentColor()
    Surface(
        modifier = modifier,
        shape = KoupaShapes.BadgeShape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = KoupaSpacing.md, vertical = KoupaSpacing.xs)
        )
    }
}

// ── KoupaChip ───────────────────────────────────────────────────────────────────
@Composable
fun KoupaChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .clip(KoupaShapes.ChipShape)
            .clickable(onClick = onClick),
        shape = KoupaShapes.ChipShape,
        color = if (selected) colorScheme.primaryContainer else colorScheme.surface,
        contentColor = if (selected) colorScheme.onPrimaryContainer else colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = KoupaSpacing.md, vertical = KoupaSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(KoupaSpacing.IconSizeSmall)
                )
                Spacer(modifier = Modifier.width(KoupaSpacing.xs))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

// ── KoupaInputField (Outlined) ─────────────────────────────────────────────────
@Composable
fun KoupaInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) colorScheme.error else colorScheme.onSurface,
            modifier = Modifier.padding(bottom = KoupaSpacing.xs)
        )

        // Input container
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { isFocused = true },
            shape = KoupaShapes.InputShape,
            placeholder = { Text(placeholder) },
            trailingIcon = icon?.let { { Icon(it, contentDescription = null) } },
            keyboardOptions = keyboardOptions,
            isError = isError,
            supportingText = supportingText?.let { { Text(it) } },
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outlineVariant,
                errorBorderColor = colorScheme.error
            )
        )
    }
}

// ── KoupaSectionHeader ─────────────────────────────────────────────────────────
@Composable
fun KoupaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KoupaSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (actionText != null && onAction != null) {
            TextButton(
                onClick = onAction,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ── KoupaIconBox ───────────────────────────────────────────────────────────────
@Composable
fun KoupaIconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = KoupaSpacing.IconBoxSize,
    iconSize: Dp = KoupaSpacing.IconSize,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(KoupaShapes.IconBoxShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

// ── KoupaIconBoxSecondary (Tonal) ─────────────────────────────────────────────
@Composable
fun KoupaIconBoxSecondary(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = KoupaSpacing.IconBoxSize,
    iconSize: Dp = KoupaSpacing.IconSize
) {
    val colorScheme = MaterialTheme.colorScheme
    KoupaIconBox(
        icon = icon,
        modifier = modifier,
        size = size,
        iconSize = iconSize,
        containerColor = colorScheme.secondaryContainer,
        contentColor = colorScheme.onSecondaryContainer
    )
}

// ── KoupaIconBoxTertiary (Surface) ──────────────────────────────────────────────
@Composable
fun KoupaIconBoxTertiary(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = KoupaSpacing.IconBoxSize,
    iconSize: Dp = KoupaSpacing.IconSize
) {
    val colorScheme = MaterialTheme.colorScheme
    KoupaIconBox(
        icon = icon,
        modifier = modifier,
        size = size,
        iconSize = iconSize,
        containerColor = colorScheme.tertiaryContainer,
        contentColor = colorScheme.onTertiaryContainer
    )
}

// ── KoupaAvatar ─────────────────────────────────────────────────────────────────
@Composable
fun KoupaAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = KoupaSpacing.AvatarSizeMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(KoupaShapes.AvatarShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4f).sp
            ),
            color = contentColor
        )
    }
}

// ── KoupaDivider ───────────────────────────────────────────────────────────────
@Composable
fun KoupaDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

// ── KoupaLoadingIndicator ─────────────────────────────────────────────────────
@Composable
fun KoupaLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 4.dp
    )
}

// ── KoupaShimmerLoading ───────────────────────────────────────────────────────
@Composable
fun KoupaShimmerLoading(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = KoupaShapes.CardShape
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

// ── KoupaEmptyState ────────────────────────────────────────────────────────────
@Composable
fun KoupaEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KoupaSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(KoupaSpacing.md))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(KoupaSpacing.xs))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(KoupaSpacing.md))
            KoupaButton(
                text = actionText,
                onClick = onAction
            )
        }
    }
}

// ── KoupaErrorState ─────────────────────────────────────────────────────────────
@Composable
fun KoupaErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KoupaSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(KoupaSpacing.md))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(KoupaSpacing.md))
        KoupaOutlinedButton(
            text = "إعادة المحاولة",
            onClick = onRetry
        )
    }
}

// ── KoupaSnackbar ────────────────────────────────────────────────────────────────
@Composable
fun KoupaSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    isError: Boolean = false
) {
    val containerColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.inverseSurface
    }
    val contentColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.inverseOnSurface
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(KoupaSpacing.md),
        shape = KoupaShapes.SnackbarShape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = KoupaElevation.SnackbarElevation,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KoupaSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ── KoupaDialog ──────────────────────────────────────────────────────────────────
@Composable
fun KoupaDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "تأكيد",
    dismissText: String = "إلغاء",
    isDanger: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = KoupaShapes.DialogShape,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = if (isDanger) {
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    ButtonDefaults.textButtonColors()
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

// ── KoupaPremiumBadge ───────────────────────────────────────────────────────────
@Composable
fun KoupaPremiumBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = KoupaShapes.BadgeShape,
        color = KoupaExtendedColors.GoldLight,
        contentColor = KoupaExtendedColors.GoldDark
    ) {
        Row(
            modifier = Modifier.padding(horizontal = KoupaSpacing.sm, vertical = KoupaSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(KoupaSpacing.xs))
            Text(
                text = "مميز",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

// ── KoupaCashBadge ───────────────────────────────────────────────────────────────
@Composable
fun KoupaCashBadge(
    amount: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = KoupaShapes.BadgeShape,
        color = KoupaExtendedColors.CashContainer,
        contentColor = KoupaExtendedColors.Cash
    ) {
        Row(
            modifier = Modifier.padding(horizontal = KoupaSpacing.sm, vertical = KoupaSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(KoupaSpacing.xs))
            Text(
                text = amount,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

// ── KoupaRatingBadge ────────────────────────────────────────────────────────────
@Composable
fun KoupaRatingBadge(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = KoupaShapes.BadgeShape,
        color = KoupaExtendedColors.GoldLight,
        contentColor = KoupaExtendedColors.GoldDark
    ) {
        Row(
            modifier = Modifier.padding(horizontal = KoupaSpacing.sm, vertical = KoupaSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = KoupaExtendedColors.Gold
            )
            Spacer(modifier = Modifier.width(KoupaSpacing.xs))
            Text(
                text = "%.1f".format(rating),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
