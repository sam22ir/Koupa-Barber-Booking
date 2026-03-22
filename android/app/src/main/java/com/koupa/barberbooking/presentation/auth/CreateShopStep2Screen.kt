package com.koupa.barberbooking.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** All services offered, shown as selectable chips. */
private val AllServices = listOf(
    "قص شعر 💇‍♂️", "حلاقة لحية 🪒", "صباغة شعر 🎨", "تنظيف بشرة ✨",
    "علاج الشعر 💆‍♂️", "قص أطفال 👦", "مكياج عرائس 💄", "ضبط حاجب ✂️"
)

/**
 * Step 2 of 3: Barber selects services, sets working hours and price range.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShopStep2Screen(
    ownerId    : String,
    googleUid  : String?,
    isEditMode : Boolean = false,
    onBack     : () -> Unit,
    onSuccess  : () -> Unit,
    viewModel  : CreateShopViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.resetSuccess()
            onSuccess()
        }
    }

    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaGold = Color(0xFFE1A553)
    val BgColor   = Color(0xFFF8F9FB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إنشاء صالونك", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("الخطوة 2 من 3", fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgColor)
            )
        },
        containerColor = BgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Progress bar (2/3 filled)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        Modifier.weight(1f).height(4.dp)
                            .background(
                                if (i < 2) KoupaTeal else Color(0xFFDDE1E5),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            // ── Services ──────────────────────────────────────────────────────────
            Spacer(Modifier.height(20.dp))
            Text("الخدمات المقدمة", fontSize = 11.sp, color = KoupaTeal,
                fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Spacer(Modifier.height(12.dp))

            val chunked = AllServices.chunked(2)
            chunked.forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { service ->
                        val isSelected = service in state.services
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) KoupaTeal else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) KoupaTeal else Color(0xFFBDC9C8),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.onServiceToggled(service) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                service,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color(0xFF3E4948),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    // If row has only 1 item, add spacer
                    if (row.size < 2) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
            }

            // ── Working Hours ─────────────────────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Text("أوقات العمل", fontSize = 11.sp, color = KoupaTeal,
                fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.openingFrom,
                    onValueChange = viewModel::onOpeningFromChanged,
                    label = { Text("من الساعة") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("09:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.openingTo,
                    onValueChange = viewModel::onOpeningToChanged,
                    label = { Text("إلى الساعة") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("20:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // ── Prices ────────────────────────────────────────────────────────────
            Spacer(Modifier.height(16.dp))
            Text("نطاق الأسعار", fontSize = 11.sp, color = KoupaTeal,
                fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = if (state.priceMin == 0) "" else state.priceMin.toString(),
                    onValueChange = { viewModel.onPriceMinChanged(it.toIntOrNull() ?: 0) },
                    label = { Text("من (د.ج)") },
                    placeholder = { Text("200") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = if (state.priceMax == 0) "" else state.priceMax.toString(),
                    onValueChange = { viewModel.onPriceMaxChanged(it.toIntOrNull() ?: 0) },
                    label = { Text("إلى (د.ج)") },
                    placeholder = { Text("2000") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Error
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            // Bottom buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(50)
                ) { Text("رجوع") }

                Button(
                    onClick = {
                        if (isEditMode) viewModel.updateShop()
                        else viewModel.createShop(ownerId, googleUid)
                    },
                    modifier = Modifier.weight(2f).height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            if (isEditMode) "حفظ التعديلات ✓"
                            else "إنشاء الصالون 🎉",
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
