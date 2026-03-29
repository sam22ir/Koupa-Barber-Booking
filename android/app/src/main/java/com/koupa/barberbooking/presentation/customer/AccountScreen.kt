package com.koupa.barberbooking.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.koupa.barberbooking.presentation.components.CustomerBottomNav
import com.koupa.barberbooking.presentation.components.CustomerTab
import com.koupa.barberbooking.ui.theme.*
import com.koupa.barberbooking.data.local.UserPreferences

@Composable
fun AccountScreen(
    userPreferences: UserPreferences,
    onNavigateToHome: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showPhoneDialog by remember { mutableStateOf(false) }
    val userName = userPreferences.getUserName()
    val savedPhone = userPreferences.getPhoneNumber()

    // Show phone dialog if no phone saved
    LaunchedEffect(savedPhone) {
        if (savedPhone.isNullOrEmpty()) {
            showPhoneDialog = true
        }
    }

    // Phone number dialog
    if (showPhoneDialog) {
        PhoneNumberDialog(
            currentPhone = savedPhone ?: "",
            onPhoneSaved = { phone ->
                userPreferences.savePhoneNumber(phone)
                showPhoneDialog = false
            },
            onDismiss = { showPhoneDialog = false }
        )
    }

    Scaffold(
        bottomBar = {
            CustomerBottomNav(
                selectedTab = CustomerTab.ACCOUNT,
                onTabSelected = { tab ->
                    when (tab) {
                        CustomerTab.HOME         -> onNavigateToHome()
                        CustomerTab.APPOINTMENTS -> onNavigateToAppointments()
                        CustomerTab.ACCOUNT      -> {}
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Premium Header ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KoupaTeal)
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar with gold border
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(KoupaGold)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = KoupaTeal,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = savedPhone ?: "مستخدم كوبا",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (userName.isNullOrEmpty()) "عميل" else userName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Menu Items ───────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KoupaSpacing.md),
                verticalArrangement = Arrangement.spacedBy(KoupaSpacing.sm)
            ) {
                AccountMenuItem(
                    icon = Icons.Default.Edit,
                    label = "تعديل الملف الشخصي",
                    subtitle = "تحديث المعلومات الشخصية",
                    onClick = onNavigateToEditProfile
                )
                AccountMenuItem(
                    icon = Icons.Default.Language,
                    label = "اللغة",
                    subtitle = "تغيير لغة التطبيق",
                    onClick = onNavigateToLanguage
                )
                AccountMenuItem(
                    icon = Icons.Default.Notifications,
                    label = "الإشعارات",
                    subtitle = "إدارة تفضيلات الإشعارات",
                    onClick = onNavigateToNotifications
                )
                AccountMenuItem(
                    icon = Icons.Default.Help,
                    label = "المساعدة",
                    subtitle = "الدعم الفني والأسئلة الشائعة",
                    onClick = onNavigateToHelp
                )
                AccountMenuItem(
                    icon = Icons.Default.Info,
                    label = "حول كوبا",
                    subtitle = "الإصدار 1.0.0 — حقوق محفوظة 2026",
                    onClick = onNavigateToAbout
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Logout Button ────────────────────────────────────────────────
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KoupaSpacing.md)
                    .height(52.dp),
                shape = KoupaShapes.ButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = KoupaError
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = KoupaError.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = KoupaError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(KoupaSpacing.sm))
                    Text(
                        text = "تسجيل الخروج",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = KoupaError
                        )
                    )
                }
            }

            Spacer(Modifier.height(KoupaSpacing.md))

            Text(
                text = "KOUPA v1.0.0\nكوبا — منصة حجز الحلاقة الجزائرية",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = KoupaSpacing.md)
            )
        }
    }
}

@Composable
private fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    KoupaCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(KoupaSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KoupaIconBox(
                icon = icon,
                size = 44.dp,
                iconSize = 22.dp
            )
            Spacer(Modifier.width(KoupaSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = KoupaDarkSlate
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = KoupaDarkSlate.copy(alpha = 0.6f)
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = KoupaDarkSlate.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Dialog for entering/editing phone number
 */
@Composable
private fun PhoneNumberDialog(
    currentPhone: String,
    onPhoneSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var phone by remember { mutableStateOf(currentPhone) }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "أدخل رقم هاتفك",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = KoupaTeal
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "سيتم استخدام هذا الرقم للحجز",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KoupaDarkSlate.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Phone input
                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue ->
                        // Only allow digits, max 10 (including 0)
                        val filtered = newValue.filter { it.isDigit() }.take(10)
                        phone = filtered
                        error = null
                    },
                    label = { Text("رقم الهاتف") },
                    placeholder = { Text("05XXXXXXXX") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Text("🇩🇿 +213")
                    },
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (phone.length < 9) {
                                error = "أدخل رقم هاتف صحيح"
                            } else {
                                // Format to +213
                                val formatted = if (phone.startsWith("0")) {
                                    "+213${phone.substring(1)}"
                                } else {
                                    "+213$phone"
                                }
                                onPhoneSaved(formatted)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KoupaTeal
                        )
                    ) {
                        Text("حفظ")
                    }
                }
            }
        }
    }
}