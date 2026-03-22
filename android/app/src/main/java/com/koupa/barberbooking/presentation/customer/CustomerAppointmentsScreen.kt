package com.koupa.barberbooking.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koupa.barberbooking.presentation.components.CustomerBottomNav
import com.koupa.barberbooking.presentation.components.CustomerTab
import com.koupa.barberbooking.domain.model.AppointmentStatus
import com.koupa.barberbooking.ui.theme.KoupaBackground
import com.koupa.barberbooking.ui.theme.KoupaError
import com.koupa.barberbooking.ui.theme.KoupaTeal
import com.koupa.barberbooking.ui.theme.KoupaGold

// Kept for UI display mapping only
private fun AppointmentStatus.displayLabel() = when (this) {
    AppointmentStatus.PENDING -> "قيد الانتظار"
    AppointmentStatus.CONFIRMED -> "مؤكد"
    AppointmentStatus.CANCELLED -> "ملغي"
    AppointmentStatus.COMPLETED -> "مكتمل"
}
private fun AppointmentStatus.displayColor() = when (this) {
    AppointmentStatus.CONFIRMED -> Color(0xFF1A7A78)
    AppointmentStatus.PENDING -> Color(0xFFE1A553)
    AppointmentStatus.CANCELLED -> Color(0xFF9E9E9E)
    AppointmentStatus.COMPLETED -> Color(0xFF607D8B)
}

// Retained for non-logged-in placeholder display
enum class FallbackAppointmentStatus(val label: String, val color: Color) {
    CONFIRMED("مؤكد", Color(0xFF1A7A78)),
    PENDING("قيد الانتظار", Color(0xFFE1A553)),
    CANCELLED("ملغي", Color(0xFF9E9E9E))
}
data class CustomerAppointment(
    val id: String, val shopName: String, val service: String,
    val date: String, val time: String, val status: FallbackAppointmentStatus
)
private val sampleAppointments = listOf(
    CustomerAppointment("1", "صالون الفخامة", "قص شعر + ذقن", "15 أكتوبر 2024", "10:30 صباحاً", FallbackAppointmentStatus.CONFIRMED),
    CustomerAppointment("2", "كوبا كلاسيك", "تنظيف بشرة ملكي", "18 أكتوبر 2024", "04:15 مساءً", FallbackAppointmentStatus.PENDING),
    CustomerAppointment("3", "الحلاقة العصرية", "قص شعر", "12 أكتوبر 2024", "11:00 صباحاً", FallbackAppointmentStatus.CANCELLED),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerAppointmentsScreen(
    customerId: String = "",  // empty = guest, show placeholders
    viewModel: CustomerViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(CustomerTab.APPOINTMENTS) }
    var selectedFilter by remember { mutableStateOf(0) }

    val appointmentsState by viewModel.appointmentsState.collectAsStateWithLifecycle()

    // Load real appointments if user is logged in
    LaunchedEffect(customerId) {
        if (customerId.isNotEmpty()) {
            viewModel.loadAppointments(customerId)
        }
    }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            CustomerTab.HOME -> onNavigateToHome()
            CustomerTab.ACCOUNT -> onNavigateToAccount()
            else -> {}
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(shadowElevation = 1.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "حجوزاتي",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        bottomBar = {
            CustomerBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs: القادمة / السابقة
            TabRow(
                selectedTabIndex = selectedFilter,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = KoupaTeal,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 },
                    text = {
                        Text(
                            "القادمة",
                            fontWeight = if (selectedFilter == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 },
                    text = {
                        Text(
                            "السابقة",
                            fontWeight = if (selectedFilter == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            val filtered = if (selectedFilter == 0)
                sampleAppointments.filter { it.status != FallbackAppointmentStatus.CANCELLED }
            else
                sampleAppointments.filter { it.status == FallbackAppointmentStatus.CANCELLED }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { appt ->
                    AppointmentCard(appointment = appt)
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(appointment: CustomerAppointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status + Shop name row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = appointment.status.color.copy(alpha = 0.12f)
                ) {
                    Text(
                        appointment.status.label,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = appointment.status.color,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                // Shop name + service
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        appointment.shopName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            appointment.service,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ContentCut, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // Date + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        appointment.time,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("الوقت", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                // Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        appointment.date,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("التاريخ", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }

            if (appointment.status != FallbackAppointmentStatus.CANCELLED) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cancel button
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KoupaError),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KoupaError.copy(alpha = 0.5f))
                    ) {
                        Text("إلغاء", fontWeight = FontWeight.SemiBold)
                    }
                    // Edit button
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal)
                    ) {
                        Text("تعديل", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
