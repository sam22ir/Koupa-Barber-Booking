package com.koupa.barberbooking.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.ui.theme.*
import com.koupa.barberbooking.utils.AlgerianPhoneFormatter

/**
 * A bottom-sheet dialog for collecting phone number during booking.
 * Used when user hasn't provided their phone number in their profile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInputDialog(
    onDismiss: () -> Unit,
    onPhoneConfirmed: (formattedPhone: String) -> Unit
) {
    var rawPhone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "معلومات التواصل",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = KoupaTeal,
                        fontSize = 20.sp
                    )
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = KoupaDarkSlate
                    )
                }
            }
            
            // Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KoupaBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📱",
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "أدخل رقم هاتفك",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = KoupaTeal
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "سيستخدم هذا الرقم للتواصل معك بشأن موعدك",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = KoupaDarkSlate,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Phone input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "رقم الجوال",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = KoupaGold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(KoupaShapes.InputShape)
                        .border(
                            width = 1.dp,
                            color = KoupaColors.Gray,
                            shape = KoupaShapes.InputShape
                        )
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country code
                    Text(
                        text = "🇩🇿 +213",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = KoupaDarkSlate
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Phone number input
                    BasicTextField(
                        value = rawPhone,
                        onValueChange = { newValue ->
                            // Only allow digits
                            if (newValue.all { it.isDigit() } && newValue.length <= 9) {
                                rawPhone = newValue
                                errorMessage = null
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = KoupaDarkSlate
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField: @Composable () -> Unit ->
                            if (rawPhone.isEmpty()) {
                                Text(
                                    text = "XXXXXXXXX",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = KoupaColors.Gray
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                
                // Error message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = KoupaError
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Confirm button
            Button(
                onClick = {
                    // Validate phone
                    val validationError = AlgerianPhoneFormatter.errorMessage(rawPhone)
                    if (validationError != null) {
                        errorMessage = validationError
                        return@Button
                    }
                    
                    // Format and confirm
                    val formattedPhone = AlgerianPhoneFormatter.format(rawPhone)
                    onPhoneConfirmed(formattedPhone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoupaTeal
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = rawPhone.isNotEmpty()
            ) {
                Text(
                    text = "تأكيد",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}
