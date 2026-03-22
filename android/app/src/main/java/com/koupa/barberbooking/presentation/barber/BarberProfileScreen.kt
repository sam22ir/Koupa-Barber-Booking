package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.koupa.barberbooking.presentation.components.BarberBottomNav
import com.koupa.barberbooking.presentation.components.BarberTab
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal

@Composable
fun BarberProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val phone = remember {
        FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "حلاق كوبا"
    }

    Scaffold(
        bottomBar = {
            BarberBottomNav(
                selectedTab = BarberTab.PROFILE,
                onTabSelected = { tab ->
                    when (tab) {
                        BarberTab.HOME          -> onNavigateToHome()
                        BarberTab.APPOINTMENTS  -> onNavigateToAppointments()
                        BarberTab.NOTIFICATIONS -> onNavigateToNotifications()
                        BarberTab.PROFILE       -> {}
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KoupaTeal)
                    .padding(vertical = 36.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(KoupaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "حلاق مسجل في كوبا",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BarberProfileMenuItem(
                    icon = Icons.Default.Store,
                    label = "بيانات المحل",
                    subtitle = "تعديل اسم ومعلومات متجرك",
                    onClick = {}
                )
                BarberProfileMenuItem(
                    icon = Icons.Default.CalendarMonth,
                    label = "إدارة المواعيد",
                    subtitle = "عرض وقبول حجوزات العملاء",
                    onClick = onNavigateToAppointments
                )
                BarberProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    label = "الإشعارات",
                    subtitle = "تنبيهات الحجز والإلغاء",
                    onClick = onNavigateToNotifications
                )
                BarberProfileMenuItem(
                    icon = Icons.Default.Info,
                    label = "حول كوبا",
                    subtitle = "الإصدار 1.0.0 — حقوق محفوظة 2026",
                    onClick = {}
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "KOUPA v1.0.0\nكوبا — منصة حجز الحلاقة الجزائرية",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun BarberProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(KoupaTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
