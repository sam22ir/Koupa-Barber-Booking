package com.koupa.barberbooking.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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

private val timeSlots = listOf(
    "09:00", "09:30", "10:00", "10:30",
    "11:00", "11:30", "12:00", "12:30",
    "13:00", "13:30", "14:00", "14:30",
    "15:00", "15:30", "16:00", "16:30"
)
// simulate some unavailable slots
private val unavailableSlots = setOf("10:30", "11:30", "13:00", "15:00")

/**
 * Step 2 of booking: Time Slot Selection
 * Matches Stitch "Book - Time Selection (Step 2)"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookTimeScreen(
    shopId: String = "",
    selectedDate: String = "الثلاثاء، ١٢ سبتمبر",
    onBack: () -> Unit = {},
    onContinue: (time: String) -> Unit = {}
) {
    var selectedTime by remember { mutableStateOf("") }

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
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { if (selectedTime.isNotEmpty()) onContinue(selectedTime) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTime.isNotEmpty()) KoupaGold else Color(0xFFE0E3E5),
                            disabledContainerColor = Color(0xFFE0E3E5)
                        ),
                        enabled = selectedTime.isNotEmpty()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowForward, contentDescription = null,
                                tint = if (selectedTime.isNotEmpty()) Color.White else Color(0xFF9E9E9E))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "استمرار للتأكيد",
                                color = if (selectedTime.isNotEmpty()) Color.White else Color(0xFF9E9E9E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
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
            // Stepper
            BookingStepper(currentStep = 1)

            Spacer(Modifier.height(20.dp))

            Text(
                "اختر الوقت المناسب",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                "المواعيد المتاحة ليوم $selectedDate",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("غير متاح", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E)))
                Spacer(Modifier.width(4.dp))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFE8E8E8)))
                Spacer(Modifier.width(12.dp))
                Text("متاح", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF9E9E9E)))
                Spacer(Modifier.width(4.dp))
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFE8F5F5)))
            }

            Spacer(Modifier.height(16.dp))

            // Time slots grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(timeSlots) { slot ->
                    val isUnavailable = slot in unavailableSlots
                    val isSelected = slot == selectedTime
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isSelected -> KoupaTeal
                                    isUnavailable -> Color(0xFFE8E8E8)
                                    else -> Color(0xFFE8F5F5)
                                }
                            )
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = if (isSelected) Color.Transparent else Color(0xFFD0D0D0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = !isUnavailable) { selectedTime = slot }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            slot,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = when {
                                    isSelected -> Color.White
                                    isUnavailable -> Color(0xFFBDBDBD)
                                    else -> KoupaTeal
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}
