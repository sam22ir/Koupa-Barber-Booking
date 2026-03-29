package com.koupa.barberbooking.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Koupa Design System Colors ──────────────────────────────────────────────
private val KoupaPrimary = Color(0xFF1A7A78)
private val KoupaPrimaryDark = Color(0xFF00605E)
private val KoupaSecondary = Color(0xFFE1A553)
private val KoupaSecondaryDark = Color(0xFF764B00)
private val KoupaBackground = Color(0xFFF3F5F7)
private val KoupaSurface = Color(0xFFF8F9FB)
private val KoupaSurfaceContainerLow = Color(0xFFF2F4F6)
private val KoupaSurfaceContainerLowest = Color(0xFFFFFFFF)
private val KoupaOnBackground = Color(0xFF191C1E)
private val KoupaOnSurfaceVariant = Color(0xFF3E4948)
private val KoupaOutline = Color(0xFF6E7978)
private val KoupaOutlineVariant = Color(0xFFBDC9C8)
private val KoupaOnPrimary = Color(0xFFFFFFFF)
private val KoupaTitleColor = Color(0xFF323E4B)

// ── Screen ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberEntryScreen(
    onPhoneSubmitted: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
) {
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        containerColor = KoupaBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = KoupaPrimaryDark,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "رجوع",
                        )
                    }
                },
                actions = {
                    Text(
                        text = "كوبا",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = KoupaPrimaryDark,
                            letterSpacing = (-0.5).sp,
                        ),
                        modifier = Modifier.padding(end = 24.dp),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KoupaSurface.copy(alpha = 0.8f),
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Background atmospheric blurs ───────────────────────────
            BackgroundDecorations()

            // ── Main content ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // ── Phone icon anchor ───────────────────────────────────
                PhoneIconAnchor()

                Spacer(modifier = Modifier.height(48.dp))

                // ── Title & subtitle ────────────────────────────────────
                Text(
                    text = "أدخل رقم هاتفك",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = KoupaTitleColor,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "سنقوم بإرسال رمز التحقق لتأكيد هويتك وضمان أمان حسابك في كوبا",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = KoupaOnSurfaceVariant,
                        lineHeight = 22.sp,
                        fontSize = 14.sp,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(280.dp),
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ── Phone input section ─────────────────────────────────
                PhoneInputSection(
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it },
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Terms text ──────────────────────────────────────────
                TermsText(
                    onTermsClick = onTermsClick,
                    onPrivacyClick = onPrivacyClick,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Continue button ─────────────────────────────────────
                ContinueButton(
                    onClick = { onPhoneSubmitted("+213$phoneNumber") },
                    enabled = phoneNumber.length >= 9,
                )

                Spacer(modifier = Modifier.weight(1f))

                // ── Help link ───────────────────────────────────────────
                HelpButton(onClick = onHelpClick)

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ── Sub-composables ────────────────────────────────────────────────────────

@Composable
private fun BackgroundDecorations() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // Top-left decorative blur
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-40).dp, y = (-60).dp)
                .blur(120.dp)
                .background(
                    KoupaPrimary.copy(alpha = 0.05f),
                    shape = CircleShape,
                ),
        )
        // Bottom-right decorative blur
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = 40.dp, y = 60.dp)
                .align(Alignment.BottomEnd)
                .blur(100.dp)
                .background(
                    KoupaSecondary.copy(alpha = 0.05f),
                    shape = CircleShape,
                ),
        )
    }
}

@Composable
private fun PhoneIconAnchor() {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        // Glow behind the icon
        Box(
            modifier = Modifier
                .size(96.dp)
                .offset(x = 16.dp, y = (-16).dp)
                .blur(48.dp)
                .background(
                    KoupaPrimary.copy(alpha = 0.1f),
                    shape = CircleShape,
                ),
        )

        // Icon container
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = KoupaSurfaceContainerLowest,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 0.5.dp,
                color = KoupaOutlineVariant.copy(alpha = 0.1f),
            ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                // Subtle primary tint overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(KoupaPrimary.copy(alpha = 0.04f)),
                )

                Icon(
                    imageVector = Icons.Filled.PhoneIphone,
                    contentDescription = null,
                    tint = KoupaPrimary,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}

@Composable
private fun PhoneInputSection(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Label
        Text(
            text = "رقم الجوال",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                color = KoupaSecondary,
            ),
            modifier = Modifier
                .padding(end = 4.dp, bottom = 8.dp)
                .align(Alignment.End),
        )

        // Input container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isFocused) KoupaPrimary.copy(alpha = 0.2f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                ),
            shape = RoundedCornerShape(16.dp),
            color = if (isFocused) KoupaSurfaceContainerLowest else KoupaSurfaceContainerLow,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Country selector chip
                CountrySelectorChip()

                // Phone number input
                BasicTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        // Only allow digits, max 9 digits (Algerian numbers)
                        val filtered = input.filter { it.isDigit() }.take(9)
                        onPhoneNumberChange(filtered)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        color = KoupaOnBackground,
                        textAlign = TextAlign.Left,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(KoupaPrimary),
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Box {
                            if (phoneNumber.isEmpty()) {
                                Text(
                                    text = "+213XXXXXXXXX",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 18.sp,
                                        letterSpacing = 1.sp,
                                        color = KoupaOutlineVariant.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Left,
                                    ),
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CountrySelectorChip() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = KoupaSurfaceContainerLowest,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable { /* TODO: Open country picker */ }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Algeria flag emoji as placeholder (replace with AsyncImage for real flag)
            Text(
                text = "\uD83C\uDDE9\uD83C\uDDFF",
                fontSize = 18.sp,
            )

            Text(
                text = "+213",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = KoupaOnBackground,
                ),
            )

            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "اختر الدولة",
                tint = KoupaOutline,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun TermsText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = KoupaOutline,
                fontSize = 11.sp,
            ),
        ) {
            append("باستمرارك، أنت توافق على ")
        }

        // Terms link
        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            style = SpanStyle(
                color = KoupaPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                textDecoration = TextDecoration.Underline,
            ),
        ) {
            append("شروط الخدمة")
        }
        pop()

        withStyle(
            style = SpanStyle(
                color = KoupaOutline,
                fontSize = 11.sp,
            ),
        ) {
            append(" و ")
        }

        // Privacy link
        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(
            style = SpanStyle(
                color = KoupaPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                textDecoration = TextDecoration.Underline,
            ),
        ) {
            append("سياسة الخصوصية")
        }
        pop()

        withStyle(
            style = SpanStyle(
                color = KoupaOutline,
                fontSize = 11.sp,
            ),
        ) {
            append(" الخاصة بنا.")
        }
    }

    androidx.compose.foundation.text.ClickableText(
        text = annotatedString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let { onTermsClick() }
            annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let { onPrivacyClick() }
        },
    )
}

@Composable
private fun ContinueButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = CircleShape,
                ambientColor = KoupaPrimary.copy(alpha = 0.4f),
                spotColor = KoupaPrimary.copy(alpha = 0.4f),
            ),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = KoupaPrimary,
            contentColor = KoupaOnPrimary,
            disabledContainerColor = KoupaPrimary.copy(alpha = 0.5f),
            disabledContentColor = KoupaOnPrimary.copy(alpha = 0.7f),
        ),
    ) {
        Text(
            text = "إرسال رمز التحقق",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            ),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun HelpButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = "هل تحتاج إلى مساعدة؟",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = KoupaSecondary,
            ),
        )
    }
}

// ── Preview ────────────────────────────────────────────────────────────────
@Preview(
    name = "Phone Entry – Light",
    showBackground = true,
    showSystemUi = true,
    locale = "ar",
)
@Composable
private fun PhoneNumberEntryScreenPreview() {
    MaterialTheme {
        PhoneNumberEntryScreen()
    }
}
