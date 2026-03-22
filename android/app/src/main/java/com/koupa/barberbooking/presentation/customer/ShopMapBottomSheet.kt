package com.koupa.barberbooking.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.koupa.barberbooking.domain.model.BarberShop

private val KoupaGold  = Color(0xFFE1A553)
private val KoupaTeal  = Color(0xFF1A7A78)
private val KoupaSlate = Color(0xFF323E4B)
private val KoupaBg    = Color(0xFFF3F5F7)

/**
 * Bottom sheet shown when a map marker is tapped.
 * Contains shop photo, name, rating, services, price range, and Book button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopMapBottomSheet(
    shop: BarberShop,
    onDismiss: () -> Unit,
    onBookNow: (shopId: String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = KoupaBg,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shop photo circle
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(KoupaSlate)
                    .border(3.dp, KoupaGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!shop.profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = shop.profilePhotoUrl,
                        contentDescription = shop.shopName,
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = KoupaGold,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Shop name
            Text(
                shop.shopName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KoupaSlate
            )

            Spacer(Modifier.height(6.dp))

            // Rating row
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i < 4) KoupaGold else Color(0xFFDDE1E5),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text("4.8", fontSize = 13.sp, color = KoupaSlate, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            // Distance / hours info
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (shop.distanceKm != null) {
                    Chip(label = "%.1f كم".format(shop.distanceKm), tint = KoupaTeal)
                }
                Chip(label = "${shop.openingFrom} – ${shop.openingTo}", tint = KoupaSlate)
            }

            Spacer(Modifier.height(12.dp))

            // Services chips
            if (shop.services.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    shop.services.take(4).forEach { svc ->
                        Chip(label = svc.substringBefore(" "), tint = KoupaTeal, outlined = true)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // Price range
            if (shop.priceMin > 0) {
                Text(
                    "من ${shop.priceMin} إلى ${shop.priceMax} د.ج",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(Modifier.height(16.dp))
            }

            // Book button
            Button(
                onClick = { onBookNow(shop.id) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal)
            ) {
                Text("احجز الآن ←", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun Chip(label: String, tint: Color, outlined: Boolean = false) {
    val bg = if (outlined) Color.Transparent else tint.copy(alpha = 0.12f)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(if (outlined) 1.dp else 0.dp, tint, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 11.sp, color = tint, fontWeight = FontWeight.Medium)
    }
}
