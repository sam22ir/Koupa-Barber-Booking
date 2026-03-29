package com.koupa.barberbooking.presentation.role

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.domain.model.UserRole
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.ui.theme.*

/**
 * Role Selection Screen — Step 2 of onboarding
 * Lets the user choose between Barber (حلاق) or Customer (زبون)
 */
@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    userPreferences: UserPreferences? = null
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KoupaBackground)
    ) {
        // ── Background decorative blurs ────────────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = (-60).dp)
                .size(260.dp)
                .clip(CircleShape)
                .background(KoupaGold.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = 420.dp)
                .size(260.dp)
                .clip(CircleShape)
                .background(KoupaTeal.copy(alpha = 0.08f))
        )

        // ── Main scrollable content ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header bar ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button placeholder (mirrors LanguageSelectionScreen)
                Spacer(modifier = Modifier.size(48.dp))

                // Logo
                Text(
                    text = "كوبا",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = KoupaTeal,
                        fontSize = 26.sp
                    )
                )

                // Spacer for centering
                Spacer(modifier = Modifier.size(48.dp))
            }

            // ── Identity section ───────────────────────────────────────────
            Text(
                text = "من أنت؟",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = KoupaDarkSlate,
                    fontSize = 36.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "اختر نوع حسابك للمتابعة",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = KoupaDarkSlate.copy(alpha = 0.65f),
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Role cards ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Barber card (right in RTL)
                RoleCard(
                    modifier = Modifier.weight(1f),
                    role = UserRole.BARBER,
                    title = "أنا حلاق",
                    subtitle = "تقديم الخدمات",
                    icon = Icons.Default.ContentCut,
                    iconTint = KoupaGold,
                    isSelected = selectedRole == UserRole.BARBER,
                    selectedColor = KoupaGold,
                    onClick = { selectedRole = UserRole.BARBER }
                )

                // Customer card (left in RTL)
                RoleCard(
                    modifier = Modifier.weight(1f),
                    role = UserRole.CUSTOMER,
                    title = "أنا زبون",
                    subtitle = "حجز المواعيد",
                    icon = Icons.Default.Person,
                    iconTint = KoupaTeal,
                    isSelected = selectedRole == UserRole.CUSTOMER,
                    selectedColor = KoupaTeal,
                    onClick = { selectedRole = UserRole.CUSTOMER }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Trust text ─────────────────────────────────────────────────
            Text(
                text = "نقوم بتخصيص تجربتك بناءً على نوع الحساب المختار لضمان أفضل خدمة.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = KoupaDarkSlate.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ── Fixed bottom button ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            KoupaBackground.copy(alpha = 0f),
                            KoupaBackground,
                            KoupaBackground
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Button(
                onClick = {
                    selectedRole?.let { onRoleSelected(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoupaTeal,
                    contentColor = Color.White,
                    disabledContainerColor = KoupaTeal.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                enabled = selectedRole != null,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "استمرار",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// Private helpers
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun RoleCard(
    modifier: Modifier = Modifier,
    role: UserRole,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else Color.Transparent,
        label = "cardBorder"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor.copy(alpha = 0.08f) else Color.White,
        label = "cardBg"
    )

    Column(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(40.dp))
            .shadow(
                elevation = if (isSelected) 10.dp else 6.dp,
                shape = RoundedCornerShape(40.dp),
                spotColor = if (isSelected) selectedColor.copy(alpha = 0.25f)
                else Color(0xFF191C1E).copy(alpha = 0.06f)
            )
            .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(40.dp)
            )
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KoupaDarkSlate,
                fontSize = 20.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Subtitle
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
                color = KoupaDarkSlate.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            textAlign = TextAlign.Center
        )

        // Selection indicator dot
        if (isSelected) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
            )
        }
    }
}
