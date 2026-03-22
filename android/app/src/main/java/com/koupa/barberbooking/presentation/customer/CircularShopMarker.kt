package com.koupa.barberbooking.presentation.customer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.koupa.barberbooking.domain.model.BarberShop

private val KoupaGold  = Color(0xFFE1A553)
private val KoupaTeal  = Color(0xFF1A7A78)
private val KoupaSlate = Color(0xFF323E4B)

/**
 * A custom round map marker for a barber shop.
 *
 * - Gold border (3dp) when not selected, Teal border when selected
 * - Shop profile photo (Coil) or fallback scissor icon on Slate bg
 * - White pill shadow card below with truncated shop name
 * - Scales up slightly when selected
 */
@Composable
fun CircularShopMarker(
    shop: BarberShop,
    isSelected: Boolean = false
) {
    // Smooth scale animation when selected
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "markerScale"
    )

    val borderColor = if (isSelected) KoupaTeal else KoupaGold
    val borderWidth = if (isSelected) 4.dp else 3.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        // Outer shadow ring
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(6.dp, CircleShape, clip = false),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(KoupaSlate)
                    .border(borderWidth, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!shop.profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = shop.profilePhotoUrl,
                        contentDescription = shop.shopName,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback icon
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = KoupaGold,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Shop name pill
        Box(
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(50))
                .background(Color.White, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = shop.shopName.take(14),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = KoupaSlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Pulsing user-location dot — teal inner dot + expanding ring animation.
 */
@Composable
fun PulsingUserDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue  = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp)) {
        // Expanding ring
        Box(
            modifier = Modifier
                .size(24.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(KoupaTeal.copy(alpha = pulseAlpha))
        )
        // Inner solid dot
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(KoupaTeal)
                .border(2.dp, Color.White, CircleShape)
        )
    }
}
