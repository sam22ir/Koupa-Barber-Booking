package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.koupa.barberbooking.presentation.components.BarberBottomNav
import com.koupa.barberbooking.presentation.components.BarberTab
import com.koupa.barberbooking.ui.theme.KoupaBackground
import com.koupa.barberbooking.ui.theme.KoupaTeal

private val weekDays = listOf("الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")

private val workSlots = listOf(
    "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
    "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
    "15:00", "15:30", "16:00", "16:30"
)

/**
 * Slot Management screen for barbers.
 * Allows enabling days and toggling time slot availability.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotManagementScreen(
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(BarberTab.APPOINTMENTS) }
    var activeDays by remember { mutableStateOf(setOf("الاثنين", "الثلاثاء", "الأربعاء", "الخميس")) }
    var bookedSlots by remember { mutableStateOf(setOf("10:30", "11:30", "13:00")) }
    var selectedDay by remember { mutableStateOf("الاثنين") }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            BarberTab.HOME -> onNavigateToHome()
            BarberTab.NOTIFICATIONS -> onNavigateToNotifications()
            BarberTab.PROFILE -> onNavigateToProfile()
            else -> {}
        }
    }

    Scaffold(
        containerColor = KoupaBackground,
        topBar = {
            Surface(shadowElevation = 1.dp, color = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color(0xFF323E4B))
                    }
                    Text("إدارة أوقات العمل", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.width(48.dp))
                }
            }
        },
        bottomBar = {
            BarberBottomNav(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = KoupaTeal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة وقت")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text(
                "أيام العمل",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Spacer(Modifier.height(10.dp))

            // Days of week chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End)
            ) {
                weekDays.reversed().forEach { day ->
                    val isActive = day in activeDays
                    val isSelected = day == selectedDay
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedDay = day
                            activeDays = if (isActive) activeDays - day else activeDays + day
                        },
                        label = { Text(day.take(2), style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KoupaTeal,
                            selectedLabelColor = Color.White,
                            containerColor = if (isActive) Color(0xFFE8F5F5) else Color.White,
                            labelColor = if (isActive) KoupaTeal else Color(0xFF9E9E9E)
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "← اضغط لتفعيل/تعطيل",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E))
                )
                Text(
                    "ساعات العمل — $selectedDay",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("محجوز", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E)))
                Spacer(Modifier.width(4.dp))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFEBE9)))
                Spacer(Modifier.width(12.dp))
                Text("متاح", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E)))
                Spacer(Modifier.width(4.dp))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFE8F5F5)))
            }

            Spacer(Modifier.height(12.dp))

            // Slots grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(workSlots) { slot ->
                    val isBooked = slot in bookedSlots
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isBooked) Color(0xFFFFEBE9) else Color(0xFFE8F5F5))
                            .border(
                                1.dp,
                                if (isBooked) Color(0xFFFFCDD2) else Color(0xFFB2DFDB),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                bookedSlots = if (isBooked) bookedSlots - slot else bookedSlots + slot
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            slot,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isBooked) Color(0xFFE53935) else KoupaTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
