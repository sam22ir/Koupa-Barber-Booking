package com.koupa.barberbooking.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal
import kotlinx.coroutines.delay

/**
 * Animated splash screen — scale+fade in, auto-navigate after 2.4s.
 * Fully respects dark / light theme via MaterialTheme.colorScheme.
 */
@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    // ── Animation states ──────────────────────────────────────────────────────
    val logoAlpha  by animateFloatAsState(targetValue = 1f, animationSpec = tween(800, easing = EaseOutCubic), label = "logoAlpha")
    val logoScale  by animateFloatAsState(targetValue = 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "logoScale")
    val subtitleAlpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(700, delayMillis = 400, easing = EaseOutCubic), label = "subtitleAlpha")

    var triggerAnim by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        triggerAnim = true
        delay(2400L)
        onSplashFinished()
    }

    // Animated values driven by triggerAnim state
    val animLogoAlpha by animateFloatAsState(
        targetValue = if (triggerAnim) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "animLogoAlpha"
    )
    val animLogoScale by animateFloatAsState(
        targetValue = if (triggerAnim) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "animLogoScale"
    )
    val animSubAlpha by animateFloatAsState(
        targetValue = if (triggerAnim) 1f else 0f,
        animationSpec = tween(700, delayMillis = 400, easing = EaseOutCubic),
        label = "animSubAlpha"
    )
    val animDividerWidth by animateFloatAsState(
        targetValue = if (triggerAnim) 1f else 0f,
        animationSpec = tween(600, delayMillis = 300, easing = EaseOutCubic),
        label = "animDividerWidth"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Arabic logo — scale + fade
            Text(
                text = "كوبا",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = KoupaTeal,
                    fontSize = 52.sp,
                    letterSpacing = (-1).sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(animLogoScale)
                    .alpha(animLogoAlpha)
            )

            // Gold divider line — grows from centre
            Box(
                modifier = Modifier
                    .width(48.dp * animDividerWidth)
                    .height(2.dp)
                    .background(KoupaGold)
                    .alpha(animDividerWidth)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Latin logo
            Text(
                text = "KOUPA",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = KoupaTeal,
                    letterSpacing = 6.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(animLogoScale)
                    .alpha(animLogoAlpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline — delayed fade-in
            Text(
                text = "THE DIGITAL ATELIER",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 3.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animSubAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PREMIUM GROOMING CONCIERGE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 2.sp,
                    fontSize = 9.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animSubAlpha)
            )
        }
    }
}
