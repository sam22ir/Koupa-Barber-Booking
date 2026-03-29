package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.domain.model.Debt
import com.koupa.barberbooking.ui.theme.*

@Composable
fun DebtSummaryCard(
    totalDebt: Int,
    unpaidCount: Int,
    onAddDebt: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add debt button
                IconButton(
                    onClick = onAddDebt,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "إضافة دين",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // DEBTS label
                Text(
                    "الديون",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                )
            }
            Spacer(Modifier.height(12.dp))
            
            // Total debt amount
            Text(
                "$totalDebt د.ج",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 36.sp
                )
            )
            
            // Unpaid count
            if (unpaidCount > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "$unpaidCount ديون غير مدفوعة",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun DebtRow(
    debt: Debt,
    onMarkAsPaid: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (debt.isPaid) Color(0xFF4CAF50) else Color(0xFFFF5722)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Colored left border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Amount
                    Text(
                        "${debt.amount} د.ج",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (debt.isPaid) Color(0xFF4CAF50) else Color(0xFFFF5722)
                        )
                    )
                    
                    // Customer name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            debt.customerName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = KoupaDarkSlate
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                debt.customerName.firstOrNull()?.toString() ?: "?",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = KoupaDarkSlate,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                
                // Notes if available
                if (!debt.notes.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        debt.notes,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = KoupaColors.Gray
                        ),
                        maxLines = 2
                    )
                }
                
                // Status and actions
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!debt.isPaid) {
                            // Mark as paid button
                            IconButton(
                                onClick = onMarkAsPaid,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "تحديد كمدفوع",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        // Edit button
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "تعديل",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        // Delete button
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (debt.isPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Text(
                            if (debt.isPaid) "مدفوع" else "غير مدفوع",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (debt.isPaid) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditDebtDialog(
    title: String,
    initialCustomerName: String = "",
    initialAmount: Int = 0,
    initialNotes: String? = null,
    initialIsPaid: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (customerName: String, amount: Int, notes: String?) -> Unit
) {
    var customerName by remember { mutableStateOf(initialCustomerName) }
    var amount by remember { mutableStateOf(if (initialAmount > 0) initialAmount.toString() else "") }
    var notes by remember { mutableStateOf(initialNotes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = KoupaDarkSlate
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Customer name field
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("اسم الزبون") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Amount field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() } },
                    label = { Text("المبلغ (د.ج)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                // Notes field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات (اختياري)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountInt = amount.toIntOrNull() ?: 0
                    if (customerName.isNotBlank() && amountInt > 0) {
                        onConfirm(customerName, amountInt, notes.ifBlank { null })
                    }
                },
                enabled = customerName.isNotBlank() && (amount.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
