package com.koupa.barberbooking.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koupa.barberbooking.domain.model.AlgeriaWilayas

/**
 * Step 1 of 3: Barber enters basic shop information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShopStep1Screen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: CreateShopViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showWilayaPicker by remember { mutableStateOf(false) }

    val KoupaTeal = Color(0xFF1A7A78)
    val BgColor   = Color(0xFFF8F9FB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إنشاء صالونك", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("الخطوة 1 من 3", fontSize = 12.sp, color = Color(0xFF6B7280))
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
            // Progress bar
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        Modifier.weight(1f).height(4.dp)
                            .background(
                                if (i == 0) KoupaTeal else Color(0xFFDDE1E5),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("المعلومات الأساسية", fontSize = 11.sp, color = KoupaTeal, fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp)
            Spacer(Modifier.height(16.dp))

            // Shop Name
            OutlinedTextField(
                value = state.shopName,
                onValueChange = viewModel::onShopNameChanged,
                label = { Text("اسم الصالون *") },
                placeholder = { Text("مثال: صالون الأناقة", color = Color(0xFFADB5BD)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Wilaya (Dropdown)
            ExposedDropdownMenuBox(
                expanded = showWilayaPicker,
                onExpandedChange = { showWilayaPicker = it }
            ) {
                OutlinedTextField(
                    value = if (state.wilayaCode != null)
                        "${state.wilayaCode} - ${AlgeriaWilayas.getName(state.wilayaCode!!)}"
                    else "",
                    onValueChange = {},
                    label = { Text("الولاية *") },
                    placeholder = { Text("اختر الولاية", color = Color(0xFFADB5BD)) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showWilayaPicker)
                    }
                )
                ExposedDropdownMenu(
                    expanded = showWilayaPicker,
                    onDismissRequest = { showWilayaPicker = false }
                ) {
                    AlgeriaWilayas.codes.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text("$code - $name") },
                            onClick = {
                                viewModel.onWilayaCodeChanged(code)
                                showWilayaPicker = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // City
            OutlinedTextField(
                value = state.city,
                onValueChange = viewModel::onCityChanged,
                label = { Text("المدينة *") },
                placeholder = { Text("مثال: حسين داي", color = Color(0xFFADB5BD)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Address
            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChanged,
                label = { Text("العنوان التفصيلي") },
                placeholder = { Text("مثال: شارع الاستقلال، رقم 15", color = Color(0xFFADB5BD)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(Modifier.height(12.dp))

            // WhatsApp
            OutlinedTextField(
                value = state.whatsappNumber,
                onValueChange = viewModel::onWhatsappChanged,
                label = { Text("رقم واتساب (اختياري)") },
                placeholder = { Text("05XXXXXXXX", color = Color(0xFFADB5BD)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                leadingIcon = { Text("📱", fontSize = 18.sp) }
            )

            // Error
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            // Next button
            Button(
                onClick = { if (viewModel.validateStep1()) onNext() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal)
            ) {
                Text("التالي ←", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
