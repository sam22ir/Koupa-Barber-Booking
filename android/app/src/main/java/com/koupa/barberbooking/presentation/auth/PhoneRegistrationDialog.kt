package com.koupa.barberbooking.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koupa.barberbooking.utils.AlgerianPhoneFormatter

/**
 * A bottom-sheet dialog shown when a guest user tries to book an appointment.
 * No OTP or verification required — just saves the phone number.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneRegistrationDialog(
    onDismiss: () -> Unit,
    onPhoneConfirmed: (formattedPhone: String) -> Unit
) {
    var rawPhone by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "أدخل رقم هاتفك للحجز",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "نحتاج رقمك فقط لتأكيد الحجز — لا يوجد تحقق بالرسائل",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = rawPhone,
                onValueChange = {
                    rawPhone = it
                    errorMsg = null
                },
                label = { Text("رقم الهاتف") },
                placeholder = { Text("0555 123 456") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                },
                supportingText = {
                    Text(errorMsg ?: "يقبل: 05XXXXXXXX أو 06XXXXXXXX أو +213XXXXXXXXX")
                },
                isError = errorMsg != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val validationError = AlgerianPhoneFormatter.errorMessage(rawPhone)
                    if (validationError != null) {
                        errorMsg = validationError
                    } else {
                        onPhoneConfirmed(AlgerianPhoneFormatter.format(rawPhone))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("تأكيد وإكمال الحجز")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    }
}
