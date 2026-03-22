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
import com.koupa.barberbooking.domain.model.AlgeriaWilayas
import com.koupa.barberbooking.domain.model.BarberShop

/**
 * Edit Shop Settings screen — lets a barber modify ALL their shop info at any time:
 *   shop name, wilaya, city, address, whatsapp, services, working hours, price range.
 *
 * Designed based on the Stitch MCP "إعدادات الصالون" screen (Koupa - Barber Booking App project).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShopScreen(
    shop      : BarberShop,
    onBack    : () -> Unit,
    onSaved   : () -> Unit,
    viewModel : CreateShopViewModel = hiltViewModel()
) {
    // Initialise once
    LaunchedEffect(shop.id) { viewModel.loadShopForEdit(shop) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showWilayaPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) { viewModel.resetSuccess(); onSaved() }
    }

    val KoupaTeal = Color(0xFF1A7A78)
    val BgColor   = Color(0xFFF8F9FB)

    val allServices = listOf(
        "قص شعر 💇‍♂️", "حلاقة لحية 🪒", "صباغة شعر 🎨", "تنظيف بشرة ✨",
        "علاج الشعر 💆‍♂️", "قص أطفال 👦", "مكياج عرائس 💄", "ضبط حاجب ✂️"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("إعدادات الصالون", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Basic Info ───────────────────────────────────────────────────────
            SectionLabel("المعلومات الأساسية", KoupaTeal)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(state.shopName, viewModel::onShopNameChanged,
                label = { Text("اسم الصالون") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(Modifier.height(10.dp))

            // Wilaya picker
            ExposedDropdownMenuBox(showWilayaPicker, { showWilayaPicker = it }) {
                OutlinedTextField(
                    value = state.wilayaCode?.let { "$it - ${AlgeriaWilayas.getName(it)}" } ?: "",
                    onValueChange = {},
                    label = { Text("الولاية") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp), readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showWilayaPicker) }
                )
                ExposedDropdownMenu(showWilayaPicker, { showWilayaPicker = false }) {
                    AlgeriaWilayas.codes.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text("$code - $name") },
                            onClick = { viewModel.onWilayaCodeChanged(code); showWilayaPicker = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(state.city, viewModel::onCityChanged,
                label = { Text("المدينة") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(state.address, viewModel::onAddressChanged,
                label = { Text("العنوان") },
                modifier = Modifier.fillMaxWidth().height(90.dp), shape = RoundedCornerShape(12.dp), maxLines = 3)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                state.whatsappNumber, viewModel::onWhatsappChanged,
                label = { Text("رقم واتساب") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Text("📱", fontSize = 18.sp) }
            )

            Spacer(Modifier.height(20.dp))

            // ── Services ────────────────────────────────────────────────────────
            SectionLabel("الخدمات المقدمة", KoupaTeal)
            Spacer(Modifier.height(12.dp))

            allServices.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { svc ->
                        val selected = svc in state.services
                        Box(
                            Modifier
                                .weight(1f).clip(RoundedCornerShape(12.dp))
                                .background(if (selected) KoupaTeal else Color.Transparent)
                                .border(1.dp, if (selected) KoupaTeal else Color(0xFFBDC9C8), RoundedCornerShape(12.dp))
                                .clickable { viewModel.onServiceToggled(svc) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(svc, fontSize = 12.sp,
                                color = if (selected) Color.White else Color(0xFF3E4948),
                                textAlign = TextAlign.Center)
                        }
                    }
                    if (row.size < 2) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))

            // ── Working Hours ───────────────────────────────────────────────────
            SectionLabel("أوقات العمل", KoupaTeal)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(state.openingFrom, viewModel::onOpeningFromChanged,
                    label = { Text("من") }, placeholder = { Text("09:00") },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(state.openingTo, viewModel::onOpeningToChanged,
                    label = { Text("إلى") }, placeholder = { Text("20:00") },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            Spacer(Modifier.height(16.dp))

            // ── Prices ──────────────────────────────────────────────────────────
            SectionLabel("نطاق الأسعار", KoupaTeal)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    if (state.priceMin == 0) "" else state.priceMin.toString(),
                    { viewModel.onPriceMinChanged(it.toIntOrNull() ?: 0) },
                    label = { Text("من (د.ج)") }, placeholder = { Text("500") },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(
                    if (state.priceMax == 0) "" else state.priceMax.toString(),
                    { viewModel.onPriceMaxChanged(it.toIntOrNull() ?: 0) },
                    label = { Text("إلى (د.ج)") }, placeholder = { Text("2000") },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            // Error
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))

            // Save button
            Button(
                onClick = viewModel::updateShop,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal),
                enabled = !state.isLoading
            ) {
                if (state.isLoading)
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else
                    Text("حفظ التعديلات ✓", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "يمكنك تعديل معلومات صالونك في أي وقت",
                fontSize = 12.sp, color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String, color: Color) {
    Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
}
