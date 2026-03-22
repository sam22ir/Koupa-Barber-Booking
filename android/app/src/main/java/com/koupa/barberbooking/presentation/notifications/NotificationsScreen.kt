package com.koupa.barberbooking.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.koupa.barberbooking.presentation.components.BarberBottomNav
import com.koupa.barberbooking.presentation.components.BarberTab
import com.koupa.barberbooking.ui.theme.KoupaBackground
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal

enum class NotifType(val icon: ImageVector, val color: Color) {
    BOOKING(Icons.Default.CalendarToday, Color(0xFF1A7A78)),
    CANCEL(Icons.Default.Cancel, Color(0xFFE53935)),
    REMINDER(Icons.Default.Alarm, Color(0xFFE1A553)),
    SYSTEM(Icons.Default.Info, Color(0xFF607D8B))
}

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val type: NotifType,
    val isRead: Boolean = false
)

private val sampleNotifications = listOf(
    AppNotification("1", "حجز جديد!", "أحمد علي حجز موعداً للساعة 10:30 صباحاً", "منذ 5 دقائق", NotifType.BOOKING, false),
    AppNotification("2", "تذكير بالموعد", "لديك موعد مع محمد رضا خلال ساعة", "منذ 23 دقيقة", NotifType.REMINDER, false),
    AppNotification("3", "إلغاء حجز", "قام ياسين سامي بإلغاء حجزه الساعة 03:00 م", "منذ ساعتين", NotifType.CANCEL, true),
    AppNotification("4", "حجز جديد!", "سامي خالد حجز موعداً ليوم غد الساعة 11:00 ص", "أمس", NotifType.BOOKING, true),
    AppNotification("5", "تحديث النظام", "تم تحديث التطبيق — تحقق من الميزات الجديدة", "منذ 3 أيام", NotifType.SYSTEM, true),
)

/**
 * Notifications screen — shared for barbers (uses BarberBottomNav).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(BarberTab.NOTIFICATIONS) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BarberTab.HOME -> onNavigateToHome()
            BarberTab.APPOINTMENTS -> onNavigateToAppointments()
            BarberTab.PROFILE -> onNavigateToProfile()
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(shadowElevation = 1.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mark all as read button
                    TextButton(onClick = {}) {
                        Text("قراءة الكل", color = KoupaTeal, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("التنبيهات", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = KoupaTeal)
                }
            }
        },
        bottomBar = {
            BarberBottomNav(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val unread = sampleNotifications.filter { !it.isRead }
            val read = sampleNotifications.filter { it.isRead }

            if (unread.isNotEmpty()) {
                item {
                    Text(
                        "جديد",
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
                items(unread) { notif -> NotificationCard(notif) }
            }

            if (read.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "السابقة",
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
                items(read) { notif -> NotificationCard(notif) }
            }
        }
    }
}

@Composable
private fun NotificationCard(notif: AppNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(if (!notif.isRead) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notif.time,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        notif.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    notif.body,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    textAlign = TextAlign.End
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(notif.type.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(notif.type.icon, contentDescription = null, tint = notif.type.color, modifier = Modifier.size(20.dp))
            }
        }
    }
}
