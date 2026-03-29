package com.koupa.barberbooking.presentation.barber

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Barber working hours screen for setting daily opening and closing times.
 * Supports RTL layout for Arabic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberWorkingHoursScreen(
 onNavigateBack: () -> Unit,
 viewModel: WorkingHoursViewModel = viewModel()
) {
 // Design tokens
 val koupaTeal = Color(0xFF1A7A78)
 val koupaGold = Color(0xFFE1A553)
 val koupaDarkSlate = Color(0xFF323E4B)
 val koupaBackground = Color(0xFFF3F5F7)

 // State for snackbar host
 val snackbarHostState = remember { SnackbarHostState() }
 val coroutineScope = rememberCoroutineScope()

 // Collect ViewModel state
 val uiState by viewModel.uiState.collectAsStateWithLifecycle()

 // Show snackbar for errors
 if (uiState.error != null) {
  LaunchedEffect(uiState.error) {
   coroutineScope.launch {
    snackbarHostState.showSnackbar(
     message = uiState.error ?: "",
     actionLabel = "إغلاق"
    )
    viewModel.clearError()
   }
  }
 }

 // Show snackbar for success
 if (uiState.isSaved) {
  LaunchedEffect(uiState.isSaved) {
   coroutineScope.launch {
    snackbarHostState.showSnackbar(
     message = "تم حفظ أوقات العمل",
     actionLabel = "إغلاق"
    )
    viewModel.clearSaved()
   }
  }
 }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "أوقات العمل",
                        color = koupaDarkSlate,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = koupaDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = koupaBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "حدد أوقات العمل اليومية",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = koupaDarkSlate,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(16.dp)
            )

  // Working hours list
  uiState.days.forEach { daySchedule ->
   DayScheduleCard(
    dayName = daySchedule.dayName,
    isOpen = daySchedule.isOpen,
    openingTime = daySchedule.openingFrom,
    closingTime = daySchedule.openingTo,
    onToggleOpen = { viewModel.toggleDay(daySchedule.dayCode) },
    onOpeningTimeClick = { /* Time picker */ },
    onClosingTimeClick = { /* Time picker */ },
    modifier = Modifier
     .fillMaxWidth()
     .padding(horizontal = 16.dp, vertical = 8.dp)
   )
  }

  // Save button
  Button(
   onClick = { viewModel.save() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = koupaTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "حفظ التغييرات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DayScheduleCard(
    dayName: String,
    isOpen: Boolean,
    openingTime: String,
    closingTime: String,
    onToggleOpen: (Boolean) -> Unit,
    onOpeningTimeClick: () -> Unit,
    onClosingTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val koupaTeal = Color(0xFF1A7A78)
    val koupaDarkSlate = Color(0xFF323E4B)
    val koupaBackground = Color(0xFFF3F5F7)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Day name and toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = koupaDarkSlate
                    )
                )
                Switch(
                    checked = isOpen,
                    onCheckedChange = onToggleOpen
                )
            }

            // Time selectors (only show if open)
            if (isOpen) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Opening time
                    OutlinedButton(
                        onClick = onOpeningTimeClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(openingTime)
                    }
                    Text(
                        text = "إلى",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        color = koupaDarkSlate
                    )
                    // Closing time
                    OutlinedButton(
                        onClick = onClosingTimeClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(closingTime)
                    }
                }
            }
        }
    }
}
