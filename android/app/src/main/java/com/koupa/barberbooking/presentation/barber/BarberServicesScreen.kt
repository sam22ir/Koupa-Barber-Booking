@file:OptIn(ExperimentalMaterial3Api::class)
package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Barber services management screen
 * Allows barbers to view, add, edit, and delete services with price ranges
 */
@Composable
fun BarberServicesScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ServicesViewModel = hiltViewModel()
) {
    // Design tokens
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaGold = Color(0xFFE1A553)
    val KoupaDarkSlate = Color(0xFF323E4B)
    val KoupaBackground = Color(0xFFF3F5F7)

    // Observe UI state from ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Dialog state
    var serviceName by remember { mutableStateOf(TextFieldValue("")) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    // Handle saved state
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaved()
            viewModel.clearSaved()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Services",
                        color = KoupaDarkSlate,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = KoupaDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KoupaBackground
                )
            )
        },
        containerColor = KoupaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Show loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KoupaTeal)
                }
            } else {
                // Services list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    itemsIndexed(uiState.services) { index, service ->
                        ServiceItemRow(
                            service = service,
                            onEdit = {
                                viewModel.showEditDialog(index)
                                serviceName = TextFieldValue(service.name)
                                minPrice = service.priceMin.toString()
                                maxPrice = service.priceMax.toString()
                            },
                            onDelete = {
                                viewModel.removeService(index)
                            },
                            KoupaDarkSlate = KoupaDarkSlate,
                            KoupaBackground = KoupaBackground
                        )
                    }
                }
            }

            // Save button
            Button(
                onClick = { viewModel.save() },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoupaTeal,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }

    // Add service FAB
    FloatingActionButton(
        onClick = {
            viewModel.showAddDialog()
            serviceName = TextFieldValue("")
            minPrice = ""
maxPrice = ""
            },
            containerColor = KoupaTeal,
            contentColor = Color.White,
            modifier = Modifier
                .padding(16.dp)
        ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Service"
        )
    }

    // Add/Edit service dialog
    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDialog() },
            title = {
                Text(
                    text = if (uiState.editingIndex == null) "Add Service" else "Edit Service",
                    color = KoupaDarkSlate
                )
            },
            text = {
                Column(
                    modifier = Modifier.width(280.dp)
                ) {
                    OutlinedTextField(
                        value = serviceName,
                        onValueChange = { serviceName = it },
                        label = { Text("Service Name") },
                        placeholder = { Text("e.g., Haircut") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = KoupaTeal,
                            unfocusedContainerColor = KoupaDarkSlate
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minPrice,
                            onValueChange = { minPrice = it.filter { c -> c.isDigit() } },
                            label = { Text("Min Price") },
                            placeholder = { Text("0") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = KoupaTeal,
                                unfocusedContainerColor = KoupaDarkSlate
                            )
                        )
                        OutlinedTextField(
                            value = maxPrice,
                            onValueChange = { maxPrice = it.filter { c -> c.isDigit() } },
                            label = { Text("Max Price") },
                            placeholder = { Text("0") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = KoupaTeal,
                                unfocusedContainerColor = KoupaDarkSlate
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = serviceName.text.trim()
                        if (name.isNotEmpty()) {
                            val min = minPrice.trim().toIntOrNull() ?: 0
                            val max = maxPrice.trim().toIntOrNull() ?: 0
                            viewModel.addService(name, min, max)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KoupaTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Error snackbar
    uiState.error?.let { error ->
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            action = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(error)
        }
    }
}

/**
 * Service item row composable
 */
@Composable
private fun ServiceItemRow(
    service: ServiceItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    KoupaDarkSlate: Color,
    KoupaBackground: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = KoupaBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = KoupaDarkSlate
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${service.priceMin} - ${service.priceMax} DZD",
                    style = MaterialTheme.typography.bodySmall,
                    color = KoupaDarkSlate.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}