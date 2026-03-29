package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit

@Composable
fun BarberSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit
) {
    // Design tokens
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaGold = Color(0xFFE1A553)
    val KoupaDarkSlate = Color(0xFF323E4B)
    val KoupaBackground = Color(0xFFF3F5F7)

    // State for toggles
    var newBookingsEnabled by rememberSaveable { mutableStateOf(true) }
    var cancellationsEnabled by rememberSaveable { mutableStateOf(true) }
    var remindersEnabled by rememberSaveable { mutableStateOf(true) }

    // Hardcoded language for now (Arabic)
    val currentLanguage = "العربية"

    val sections = listOf(
        Section(SectionType.Header, title = "الإشعارات"),
        Section(SectionType.ToggleItem, item = ToggleItem(
            title = "حجوزات جديدة",
            checked = newBookingsEnabled,
            onCheckedChange = { newBookingsEnabled = it }
        )),
        Section(SectionType.ToggleItem, item = ToggleItem(
            title = "إلغاء الحجوزات",
            checked = cancellationsEnabled,
            onCheckedChange = { cancellationsEnabled = it }
        )),
        Section(SectionType.ToggleItem, item = ToggleItem(
            title = "تذكيرات",
            checked = remindersEnabled,
            onCheckedChange = { remindersEnabled = it }
        )),
        Section(SectionType.Header, title = "اللغة"),
        Section(SectionType.NavItem, item = NavItem(
            title = currentLanguage,
            onClick = { /* Language change would be handled here */ }
        )),
        Section(SectionType.Header, title = "الحساب"),
        Section(SectionType.NavItem, item = NavItem(
            title = "عرض الملف الشخصي",
            onClick = { /* Navigate to profile */ }
        )),
        Section(SectionType.NavItem, item = NavItem(
            title = "تغيير كلمة المرور",
            onClick = { /* Navigate to change password */ }
        )),
        Section(SectionType.Header, title = "التطبيق"),
        Section(SectionType.NavItem, item = NavItem(
            title = "مسح الكاش",
            onClick = { /* Clear cache */ }
        )),
        Section(SectionType.NavItem, item = NavItem(
            title = "عن التطبيق",
            onClick = onNavigateToAbout
        )),
        Section(SectionType.LogoutButton, item = LogoutItem(
            text = "تسجيل الخروج",
            onClick = onLogout
        ))
    )

Scaffold(
        containerColor = KoupaBackground,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = KoupaDarkSlate,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KoupaBackground
                )
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                items(sections) { section ->
                    when (section.type) {
                        SectionType.Header -> {
                            SectionHeader(title = section.title ?: "")
                        }
                        SectionType.ToggleItem -> {
                            val item = section.item as ToggleItem
                            ToggleItemRow(
                                title = item.title,
                                checked = item.checked,
                                onCheckedChange = { item.onCheckedChange(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { } // Prevents click through to parent
                            )
                        }
                        SectionType.NavItem -> {
                            val item = section.item as NavItem
                            NavItemRow(
                                title = item.title,
                                onClick = { item.onClick() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { item.onClick() }
                            )
                        }
                        SectionType.LogoutButton -> {
                            val item = section.item as LogoutItem
                            LogoutButton(
                                text = item.text,
                                onClick = { item.onClick() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { item.onClick() }
                            )
                        }
                    }
                    Divider(
                        color = KoupaDarkSlate.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                }
            }
        }
    )
}

private enum class SectionType {
    Header, ToggleItem, NavItem, LogoutButton
}

private data class Section(
    val type: SectionType,
    val title: String? = null,
    val item: Any? = null
)

private data class ToggleItem(
    val title: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)

private data class NavItem(
    val title: String,
    val onClick: () -> Unit
)

private data class LogoutItem(
    val text: String,
    val onClick: () -> Unit
)

@Composable
private fun SectionHeader(title: String) {
    val KoupaDarkSlate = Color(0xFF323E4B)
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = KoupaDarkSlate,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier
            .padding(top = 24.dp, bottom = 8.dp, start = 16.dp)
    )
}

@Composable
private fun ToggleItemRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaDarkSlate = Color(0xFF323E4B)
    
    Row(
        modifier = modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = KoupaDarkSlate
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun NavItemRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val KoupaDarkSlate = Color(0xFF323E4B)
    
    Row(
        modifier = modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = KoupaDarkSlate
            ),
            modifier = Modifier.weight(1f)
        )
        // Arrow icon for navigational items
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = KoupaDarkSlate.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LogoutButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red.copy(alpha = 0.1f),
            contentColor = Color.Red
        ),
        modifier = modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}