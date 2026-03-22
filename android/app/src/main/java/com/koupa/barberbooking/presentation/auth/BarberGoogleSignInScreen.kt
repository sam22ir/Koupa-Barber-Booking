package com.koupa.barberbooking.presentation.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Barber Google Sign-In screen.
 * Phone number is already registered; this screen links the barber's Google account.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberGoogleSignInScreen(
    phoneNumber: String,
    onSignInSuccess: (googleUid: String, email: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg  by remember { mutableStateOf<String?>(null) }

    // Google Sign-In launcher
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val uid   = account?.id ?: return@rememberLauncherForActivityResult
                val email = account.email ?: ""
                onSignInSuccess(uid, email)
            } catch (e: ApiException) {
                errorMsg = "فشل تسجيل الدخول بـ Google: ${e.message}"
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val KoupaTeal  = Color(0xFF1A7A78)
    val KoupaGold  = Color(0xFFE1A553)
    val Surface    = Color(0xFFF8F9FB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface
                )
            )
        },
        containerColor = Surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Scissors icon in teal gradient circle
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(KoupaTeal, Color(0xFF00605E)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("✂", fontSize = 40.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "سجّل كحلاق",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = KoupaTeal,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "ربط حسابك بـ Google يتيح لك إدارة صالونك\nوجدولة مواعيدك بسهولة تامة",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.weight(1f))

            // Google Sign-In button
            Card(
                onClick = {
                    isLoading = true
                    errorMsg  = null
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestId()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    googleLauncher.launch(client.signInIntent)
                },
                modifier   = Modifier.fillMaxWidth().height(56.dp),
                shape      = RoundedCornerShape(16.dp),
                colors     = CardDefaults.cardColors(containerColor = Color.White),
                elevation  = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = KoupaTeal, strokeWidth = 2.dp)
                    } else {
                        Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                        Spacer(Modifier.width(12.dp))
                        Text("متابعة بحساب Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFFDDE1E5))
                Text("  أو  ", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                HorizontalDivider(Modifier.weight(1f), color = Color(0xFFDDE1E5))
            }

            Spacer(Modifier.height(12.dp))

            // Phone chip
            Box(
                modifier = Modifier
                    .border(1.dp, KoupaTeal.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("$phoneNumber ✓", fontSize = 14.sp, color = KoupaTeal)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "حسابك الحالي سيُربط بـ Google تلقائياً",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )

            // Error
            errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }

            // Progress dots
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(
                        Modifier.size(if (i == 1) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (i == 1) KoupaTeal else Color(0xFFBDC9C8))
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
