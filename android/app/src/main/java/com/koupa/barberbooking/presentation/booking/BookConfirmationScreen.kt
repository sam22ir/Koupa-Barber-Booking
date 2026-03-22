package com.koupa.barberbooking.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koupa.barberbooking.ui.theme.KoupaBackground
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal

/**
 * Step 3 of booking: Confirmation summary — calls real Supabase on confirm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConfirmationScreen(
    customerId: String = "",   // Supabase user ID (passed from auth state in nav)
    shopId: String = "",
    slotId: String = "",       // Set empty if navigated by date/time; we use time as slot fallback
    shopName: String = "صالون الفخامة",
    service: String = "قص شعر + ذقن",
    selectedDate: String = "الثلاثاء، ١٢ سبتمبر",
    selectedTime: String = "10:00",
    price: String = "800 دج",
    viewModel: BookingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // When Supabase confirms, show success screen
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(state.isConfirmed) {
        if (state.isConfirmed) {
            showSuccess = true
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    if (showSuccess) {
        BookingSuccessScreen(onDone = onConfirm)
        return
    }


    Scaffold(
        containerColor = KoupaBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    Text(
                        "تأكيد الحجز",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = KoupaTeal)
                    )
                    Spacer(Modifier.width(48.dp))
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { viewModel.confirmBooking(customerId, shopId, slotId) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("تأكيد الحجز", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                .verticalScroll(rememberScrollState())
        ) {
            BookingStepper(currentStep = 2)

            Spacer(Modifier.height(20.dp))

            Text(
                "مراجعة تفاصيل الحجز",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                "تحقق من التفاصيل قبل التأكيد",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(Modifier.height(20.dp))

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Shop header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(shopName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text("الجزائر العاصمة", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6B7280)))
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE8F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(16.dp))

                    ConfirmationRow(icon = Icons.Default.ContentCut, label = "الخدمة", value = service)
                    Spacer(Modifier.height(14.dp))
                    ConfirmationRow(icon = Icons.Default.CalendarToday, label = "التاريخ", value = selectedDate)
                    Spacer(Modifier.height(14.dp))
                    ConfirmationRow(icon = Icons.Default.Schedule, label = "الوقت", value = selectedTime)
                    Spacer(Modifier.height(14.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(Modifier.height(14.dp))
                    ConfirmationRow(icon = Icons.Default.Payments, label = "السعر", value = price, valueColor = KoupaTeal)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "يمكنك إلغاء الحجز أو تعديله حتى ساعتين قبل الموعد",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8D6E14)),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Info, contentDescription = null, tint = KoupaGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun ConfirmationRow(icon: ImageVector, label: String, value: String, valueColor: Color = Color(0xFF323E4B)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = valueColor))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)))
            Spacer(Modifier.width(8.dp))
            Icon(icon, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun BookingSuccessScreen(onDone: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(KoupaBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(80.dp))
            Text("تم تأكيد حجزك!", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = KoupaTeal))
            Text("سنرسل لك تذكيراً قبل موعدك", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6B7280)), textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("العودة للرئيسية", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
