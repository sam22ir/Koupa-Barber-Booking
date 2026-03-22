package com.koupa.barberbooking.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.koupa.barberbooking.ui.theme.KoupaTeal

// ─── Customer Bottom Nav — 3 tabs ─────────────────────────────────────────────

enum class CustomerTab(val label: String, val icon: ImageVector) {
    HOME("الرئيسية", Icons.Default.Home),
    APPOINTMENTS("مواعيدي", Icons.Default.CalendarMonth),
    ACCOUNT("حسابي", Icons.Default.AccountCircle)
}

@Composable
fun CustomerBottomNav(
    selectedTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor   = KoupaTeal,
        modifier       = Modifier.height(64.dp),
        tonalElevation = 4.dp
    ) {
        CustomerTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val iconTint by animateColorAsState(
                targetValue = if (selected) KoupaTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "customerIconTint"
            )
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = iconTint
                    )
                },
                label = {
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = KoupaTeal,
                    selectedTextColor   = KoupaTeal,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = KoupaTeal.copy(alpha = 0.15f)
                )
            )
        }
    }
}

// ─── Barber Bottom Nav — 4 tabs ───────────────────────────────────────────────

enum class BarberTab(val label: String, val icon: ImageVector) {
    HOME("الرئيسية", Icons.Default.Home),
    APPOINTMENTS("المواعيد", Icons.Default.CalendarMonth),
    NOTIFICATIONS("التنبيهات", Icons.Default.Notifications),
    PROFILE("الملف", Icons.Default.Person)
}

@Composable
fun BarberBottomNav(
    selectedTab: BarberTab,
    onTabSelected: (BarberTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor   = KoupaTeal,
        modifier       = Modifier.height(64.dp),
        tonalElevation = 4.dp
    ) {
        BarberTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            val iconTint by animateColorAsState(
                targetValue = if (selected) KoupaTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "barberIconTint"
            )
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = iconTint
                    )
                },
                label = {
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = KoupaTeal,
                    selectedTextColor   = KoupaTeal,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = KoupaTeal.copy(alpha = 0.15f)
                )
            )
        }
    }
}
