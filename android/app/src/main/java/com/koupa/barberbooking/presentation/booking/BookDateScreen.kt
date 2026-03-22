package com.koupa.barberbooking.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.ui.theme.KoupaBackground
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal

// Days of week header
private val daysOfWeek = listOf("س", "ج", "خ", "ر", "ث", "ن", "ح")

// Sample days in a month for the calendar
private val calendarDays = (1..30).toList()

/**
 * Step 1 of booking: Date Selection
 * Matches Stitch "Book - Date Selection (Step 1)" design.
 * No Bottom Nav — it's a booking flow screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDateScreen(
    shopId: String = "",
    shopName: String = "صالون الفخامة",
    shopCity: String = "فرع الرياض الرئيسي",
    onBack: () -> Unit = {},
    onContinue: (date: String) -> Unit = {}
) {
    var selectedDay by remember { mutableStateOf(12) }
    val currentMonth = "سبتمبر ٢٠٢٣"
    val selectedDateLabel = "الثلاثاء، ١٢ سبتمبر"

    Scaffold(
        containerColor = KoupaBackground,
        topBar = {
            Surface(shadowElevation = 1.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color(0xFF323E4B))
                    }
                    Text(
                        "حجز موعد",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = KoupaTeal)
                    )
                    Spacer(Modifier.width(48.dp))
                }
            }
        },
        bottomBar = {
            // Gold CTA Button matching Stitch
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { onContinue(selectedDateLabel) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KoupaGold)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("استمرار للوقت", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 3-Step Stepper
            BookingStepper(currentStep = 0)

            Spacer(Modifier.height(20.dp))

            // Question
            Text(
                "متى تفضل الحضور؟",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                "اختر التاريخ المناسب لك من التقويم أدناه",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(Modifier.height(20.dp))

            // Calendar card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFF323E4B))
                        }
                        Text(
                            currentMonth,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF323E4B))
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Day of week headers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        daysOfWeek.forEach { day ->
                            Text(
                                day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E))
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Calendar grid
                    var dayIndex = 0
                    val weeks = 5
                    for (week in 0 until weeks) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            for (col in 0 until 7) {
                                val day = if (dayIndex < calendarDays.size) calendarDays[dayIndex++] else null
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                day == selectedDay -> KoupaTeal
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(enabled = day != null) {
                                            day?.let { selectedDay = it }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (day != null) {
                                        Text(
                                            "$day",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (day == selectedDay) Color.White else Color(0xFF323E4B),
                                                fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Selected date summary
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
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("المكان", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E)))
                        Text(shopCity, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("اليوم المحدد", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E)))
                            Text(selectedDateLabel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = KoupaTeal))
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BookingStepper(currentStep: Int) {
    val steps = listOf("اختر التاريخ", "اختر الوقت", "تأكيد")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            // Step circle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (index <= currentStep) KoupaTeal else Color(0xFFE0E3E5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (index <= currentStep) Color.White else Color(0xFF9E9E9E),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (index <= currentStep) KoupaTeal else Color(0xFF9E9E9E),
                        fontSize = 10.sp
                    )
                )
            }
            // Connector line (not after last)
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (index < currentStep) KoupaTeal else Color(0xFFE0E3E5))
                )
            }
        }
    }
}
