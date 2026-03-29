package com.koupa.barberbooking.presentation.barber

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.presentation.components.BarberBottomNav
import com.koupa.barberbooking.presentation.components.BarberTab
import com.koupa.barberbooking.ui.theme.KoupaBackground
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal

// TodayAppointment is defined in BarberDashboardViewModel.kt

/**
 * Barber Dashboard matching Stitch "Barber Dashboard" design.
 * Shows earnings, today's counts, schedule, and tip card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberDashboardScreen(
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSlotManagement: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(BarberTab.HOME) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BarberTab.APPOINTMENTS -> onNavigateToAppointments()
            BarberTab.NOTIFICATIONS -> onNavigateToNotifications()
            BarberTab.PROFILE -> onNavigateToProfile()
            else -> {}
        }
    }

    Scaffold(
        containerColor = KoupaBackground,
        topBar = {
            Surface(shadowElevation = 0.dp, color = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Settings, contentDescription = "إعدادات", tint = Color(0xFF323E4B))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "الاثنين ٢١ مارس ٢٠٢٦",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E))
                        )
                        Text(
                            "كوبا",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = KoupaTeal)
                        )
                    }
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFE8F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(22.dp))
                    }
                }
            }
        },
        bottomBar = {
            BarberBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Greeting
                Text(
                    "إليك ملخص اليوم",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                Text(
                    "مرحباً، كريم!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

            // Earnings card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(listOf(KoupaTeal, Color(0xFF0D5452)))
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "الإيرادات",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFB2DFDB)),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "1500 د.ج",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 36.sp)
                        )
                    }
                }
            }

            // Summary stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "المواعيد اليوم",
                        value = "3",
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "التقييم العام",
                        value = "4.5",
                        icon = Icons.Default.Star,
                        iconTint = KoupaGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Schedule header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onNavigateToAppointments) {
                        Text("عرض الكل", color = KoupaTeal, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        "جدول المواعيد",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

// Appointment rows
        items(
            listOf(
                TodayAppointment("1", "أحمد محمد", "قص شعر", "09:00", "مؤكد"),
                TodayAppointment("2", "كريم علي", "حلاقة لحية", "10:30", "قيد الانتظار"),
                TodayAppointment("3", "يوسف بوزيد", "قص + غسيل", "14:00", "مؤكد")
            )
        ) { appt ->
            AppointmentRow(appt)
        }

            // Tip card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FB))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text(
                            "نصيحة اليوم",
                            style = MaterialTheme.typography.labelMedium.copy(color = KoupaGold, fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "حافظ على أدواتك معقمة لضمان أفضل تجربة لزبائنك اليوم.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF323E4B)),
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Slot management CTA
                Button(
                    onClick = onNavigateToSlotManagement,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = KoupaTeal)
                    Spacer(Modifier.width(8.dp))
                    Text("إدارة أوقات العمل", color = KoupaTeal, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = KoupaTeal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF323E4B)))
            Text(label, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
        }
    }
}

@Composable
private fun AppointmentRow(appt: TodayAppointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time + status
            Column(horizontalAlignment = Alignment.Start) {
                Text(appt.time, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = KoupaTeal))
                Spacer(Modifier.height(2.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = KoupaTeal.copy(alpha = 0.1f)) {
                    Text(
                        appt.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = KoupaTeal)
                    )
                }
            }
            // Client info + avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(appt.clientName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                    Text(appt.service, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
                }
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier.size(38.dp).clip(CircleShape).background(KoupaTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        appt.clientName.first().toString(),
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
