package com.koupa.barberbooking.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.koupa.barberbooking.R
import kotlinx.coroutines.delay

/**
 * OTP verification screen for authentication.
 * Shows 6-digit OTP input with countdown timer.
 */
@Composable
fun OtpVerificationScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onVerificationSuccess: () -> Unit,
    onNavigateToRoleSelection: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Countdown timer (60 seconds)
    var timeLeft by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        canResend = true
    }

    // Navigate on success
    LaunchedEffect(uiState.isAuthenticated, uiState.showRoleSelection) {
        if (uiState.isAuthenticated) {
            onVerificationSuccess()
        } else if (uiState.showRoleSelection) {
            onNavigateToRoleSelection()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = stringResource(R.string.otp_verification_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = stringResource(R.string.otp_verification_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OTP input
        OutlinedTextField(
            value = uiState.otpCode,
            onValueChange = { viewModel.onOtpChanged(it) },
            label = { Text(stringResource(R.string.otp_label)) },
            placeholder = { Text("XXXXXX") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Error message
        if (uiState.error != null) {
            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Countdown timer
        if (!canResend) {
            Text(
                text = stringResource(R.string.resend_otp_timer, timeLeft),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TextButton(onClick = {
                viewModel.sendOtp()
                timeLeft = 60
                canResend = false
            }) {
                Text(stringResource(R.string.resend_otp_button))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Verify button
        Button(
            onClick = { viewModel.verifyOtp() },
            enabled = uiState.otpCode.length == 6 && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.verify_otp_button))
            }
        }
    }
}
