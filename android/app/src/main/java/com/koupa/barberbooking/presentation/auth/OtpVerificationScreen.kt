package com.koupa.barberbooking.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ── Koupa Design System Colors ──────────────────────────────────────────────
private val KoupaPrimary = Color(0xFF1A7A78)
private val KoupaPrimaryContainer = Color(0xFF1A7A78)
private val KoupaOnPrimary = Color.White
private val KoupaAccent = Color(0xFFE1A553)
private val KoupaDarkText = Color(0xFF323E4B)
private val KoupaOnSurface = Color(0xFF191C1E)
private val KoupaOnSurfaceVariant = Color(0xFF3E4948)
private val KoupaBackground = Color(0xFFF3F5F7)
private val KoupaSurface = Color(0xFFF8F9FB)
private val KoupaSurfaceContainerLowest = Color.White
private val KoupaSurfaceContainerLow = Color(0xFFF2F4F6)
private val KoupaOutlineVariant = Color(0xFFBDC9C8)

// ── Fonts ───────────────────────────────────────────────────────────────────
// Replace with actual font resources when available:
//   R.font.cairo_bold, R.font.cairo_semibold, R.font.cairo_regular
//   R.font.plus_jakarta_sans_bold, R.font.work_sans_regular, etc.
private val CairoFontFamily = FontFamily.Default   // TODO: Font(R.font.cairo)
private val HeadlineFontFamily = FontFamily.Default // TODO: Font(R.font.plus_jakarta_sans)
private val BodyFontFamily = FontFamily.Default     // TODO: Font(R.font.work_sans)

// ── Screen ──────────────────────────────────────────────────────────────────

/**
 * شاشة التحقق من رمز OTP
 *
 * @param phoneNumber  رقم الهاتف المعروض (اختياري للرسالة التوضيحية)
 * @param initialSeconds  المدة الابتدائية للعد التنازلي بالثواني (افتراضي 90)
 * @param onOtpVerified  يُستدعى عند إدخال الرمز كاملاً (6 أرقام) أو الضغط على تأكيد
 * @param onResendCode  يُستدعى عند الضغط على "إعادة إرسال الرمز"
 * @param onBackClick  يُستدعى عند الضغط على زر الرجوع
 * @param onContactSupport  يُستدعى عند الضغط على "تواصل مع الدعم الفني"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    phoneNumber: String = "",
    initialSeconds: Int = 90,
    onOtpVerified: (String) -> Unit = {},
    onResendCode: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onContactSupport: () -> Unit = {},
) {
    // ── OTP state ───────────────────────────────────────────────────────
    val otpLength = 6
    val otpDigits = remember { mutableStateListOf(*Array(otpLength) { "" }) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    // ── Timer state ─────────────────────────────────────────────────────
    var remainingSeconds by remember { mutableIntStateOf(initialSeconds) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning, remainingSeconds) {
        if (isTimerRunning && remainingSeconds > 0) {
            delay(1_000L)
            remainingSeconds--
        } else if (remainingSeconds <= 0) {
            isTimerRunning = false
        }
    }

    val minutes = (remainingSeconds / 60).toString().padStart(2, '0')
    val seconds = (remainingSeconds % 60).toString().padStart(2, '0')
    val timerText = "$minutes:$seconds"

    val isComplete = otpDigits.all { it.isNotBlank() }

    // ── Helper: build code string ───────────────────────────────────────
    fun currentCode(): String = otpDigits.joinToString("")

    // ── Auto-verify when all digits entered ─────────────────────────────
    LaunchedEffect(isComplete) {
        if (isComplete) {
            onOtpVerified(currentCode())
        }
    }

    // ── RTL wrapper ─────────────────────────────────────────────────────
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = KoupaBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "كوبا",
                            fontFamily = HeadlineFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = KoupaPrimary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                // Use arrow_forward and rely on RTL mirroring
                                painter = painterResource(
                                    id = android.R.drawable.ic_media_play // placeholder
                                ),
                                contentDescription = "رجوع",
                                tint = KoupaPrimary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KoupaSurface.copy(alpha = 0.8f),
                    ),
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // ── Icon circle ─────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(KoupaPrimaryContainer.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.ic_dialog_info // placeholder for app_registration
                        ),
                        contentDescription = null,
                        tint = KoupaPrimary,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Title ───────────────────────────────────────────────
                Text(
                    text = "أدخل رمز التحقق",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = KoupaDarkText,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── Subtitle ────────────────────────────────────────────
                Text(
                    text = "أدخل الرمز المكون من 6 أرقام المرسل إلى هاتفك",
                    fontFamily = BodyFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = KoupaOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.width(280.dp),
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ── OTP boxes ───────────────────────────────────────────
                OtpInputRow(
                    otpDigits = otpDigits,
                    focusRequesters = focusRequesters,
                    onDigitChanged = { index, value ->
                        if (value.length <= 1 && value.all { it.isDigit() }) {
                            otpDigits[index] = value
                            if (value.isNotEmpty() && index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    onBackspace = { index ->
                        if (otpDigits[index].isEmpty() && index > 0) {
                            otpDigits[index - 1] = ""
                            focusRequesters[index - 1].requestFocus()
                        }
                    },
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ── Timer ───────────────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.ic_lock_idle_lock // placeholder for timer icon
                        ),
                        contentDescription = null,
                        tint = KoupaAccent,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timerText,
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = KoupaAccent,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Resend button ───────────────────────────────────────
                TextButton(
                    onClick = {
                        remainingSeconds = initialSeconds
                        isTimerRunning = true
                        otpDigits.forEachIndexed { index, _ -> otpDigits[index] = "" }
                        focusRequesters.first().requestFocus()
                        onResendCode()
                    },
                    enabled = !isTimerRunning,
                ) {
                    Text(
                        text = "إعادة إرسال الرمز",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isTimerRunning) KoupaPrimary.copy(alpha = 0.4f) else KoupaPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Confirm button ──────────────────────────────────────
                Button(
                    onClick = {
                        if (isComplete) onOtpVerified(currentCode())
                    },
                    enabled = isComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KoupaPrimary,
                        disabledContainerColor = KoupaPrimary.copy(alpha = 0.4f),
                        contentColor = KoupaOnPrimary,
                        disabledContentColor = KoupaOnPrimary.copy(alpha = 0.6f),
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                    ),
                ) {
                    Text(
                        text = "تأكيد",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.ic_media_play // placeholder for arrow_forward
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                // ── Support link ────────────────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KoupaSurfaceContainerLow,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = KoupaOutlineVariant.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp),
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = buildString {
                                append("هل تواجه مشكلة؟ ")
                            },
                            fontFamily = BodyFontFamily,
                            fontSize = 12.sp,
                            color = KoupaOnSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // Inline support link version (alternative to above)
                // Uncomment this block and remove the Surface above if you
                // prefer a clickable "تواصل مع الدعم الفني" link.
                /*
                TextButton(onClick = onContactSupport) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = KoupaOnSurfaceVariant)) {
                                append("هل تواجه مشكلة؟ ")
                            }
                            withStyle(
                                SpanStyle(
                                    color = KoupaPrimary,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                            ) {
                                append("تواصل مع الدعم الفني")
                            }
                        },
                        fontFamily = BodyFontFamily,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                */

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ── OTP Input Row ───────────────────────────────────────────────────────────

@Composable
private fun OtpInputRow(
    otpDigits: MutableList<String>,
    focusRequesters: List<FocusRequester>,
    onDigitChanged: (index: Int, value: String) -> Unit,
    onBackspace: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in otpDigits.indices) {
            OtpCell(
                value = otpDigits[i],
                onValueChange = { onDigitChanged(i, it) },
                onBackspace = { onBackspace(i) },
                isFocused = false, // simplified — Compose handles focus visuals
                modifier = Modifier
                    .focusRequester(focusRequesters[i])
                    .size(width = 48.dp, height = 56.dp),
            )
        }
    }
}

// ── Single OTP Cell ─────────────────────────────────────────────────────────

@Composable
private fun OtpCell(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    isFocused: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty()) {
                onBackspace()
            } else {
                onValueChange(newValue.takeLast(1))
            }
        },
        modifier = modifier
            .clip(shape)
            .background(KoupaSurfaceContainerLowest)
            .border(
                width = 1.5.dp,
                color = if (value.isNotEmpty()) KoupaPrimary else Color.Transparent,
                shape = shape,
            ),
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KoupaOnSurface,
            textAlign = TextAlign.Center,
            fontFamily = HeadlineFontFamily,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        cursorBrush = SolidColor(KoupaPrimary),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                innerTextField()
            }
        },
    )
}

// ── Preview ─────────────────────────────────────────────────────────────────

@Preview(
    name = "OTP Verification — RTL",
    locale = "ar",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun OtpVerificationScreenPreview() {
    MaterialTheme {
        OtpVerificationScreen(
            onOtpVerified = {},
            onResendCode = {},
            onBackClick = {},
            onContactSupport = {},
        )
    }
}
