package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberStatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = viewModel()
) {
    // Define colors from design tokens
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaGold = Color(0xFFE1A553)
    val KoupaDarkSlate = Color(0xFF323E4B)
    val KoupaBackground = Color(0xFFF3F5F7)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "الإحصائيات",
                        color = KoupaDarkSlate,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = KoupaDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = KoupaBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Period Selector
            PeriodSelector(
                selectedPeriod = when (uiState.selectedPeriod) {
                    "weekly" -> Period.Weekly
                    "monthly" -> Period.Monthly
                    "yearly" -> Period.Yearly
                    else -> Period.Weekly
                },
                onPeriodSelected = { period ->
                    val periodString = when (period) {
                        Period.Weekly -> "weekly"
                        Period.Monthly -> "monthly"
                        Period.Yearly -> "yearly"
                    }
                    viewModel.onPeriodChanged(periodString)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            // Show loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    CircularProgressIndicator(
                        color = KoupaTeal,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                // Show error if any
                uiState.error?.let { error ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color.Red.copy(alpha = 0.1f))
                            .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Cards Row
                SummaryCardsRow(
                    totalBookings = uiState.totalBookings,
                    revenue = uiState.revenue,
                    newCustomers = uiState.newCustomers,
                    avgRating = uiState.avgRating,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Simple Bar Chart
                BookingBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Top Services List
                TopServicesList(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

enum class Period { Weekly, Monthly, Yearly }

@Composable
fun PeriodSelector(
    selectedPeriod: Period,
    onPeriodSelected: (Period) -> Unit,
    modifier: Modifier = Modifier
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaDarkSlate = Color(0xFF323E4B)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeriodTab(
            text = "أسبوعي",
            selected = selectedPeriod == Period.Weekly,
            onClick = { onPeriodSelected(Period.Weekly) }
        )
        PeriodTab(
            text = "شهري",
            selected = selectedPeriod == Period.Monthly,
            onClick = { onPeriodSelected(Period.Monthly) }
        )
        PeriodTab(
            text = "سنوي",
            selected = selectedPeriod == Period.Yearly,
            onClick = { onPeriodSelected(Period.Yearly) }
        )
    }
}

@Composable
fun PeriodTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaDarkSlate = Color(0xFF323E4B)

    val backgroundColor = if (selected) KoupaTeal else Color.Transparent
    val textColor = if (selected) Color.White else KoupaDarkSlate

    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SummaryCardsRow(
    totalBookings: Int,
    revenue: Int,
    newCustomers: Int,
    avgRating: Double,
    modifier: Modifier = Modifier
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaGold = Color(0xFFE1A553)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        SummaryCard(
            title = "إجمالي الحجوزات",
            value = totalBookings.toString(),
            icon = Icons.Default.Event,
            color = KoupaTeal
        )
        SummaryCard(
            title = "الإيرادات",
            value = "%,d دج".format(revenue),
            icon = Icons.Default.AttachMoney,
            color = KoupaGold
        )
        SummaryCard(
            title = "العملاء الجدد",
            value = newCustomers.toString(),
            icon = Icons.Default.PersonAdd,
            color = KoupaTeal
        )
        SummaryCard(
            title = "متوسط التقييم",
            value = "%.1f".format(avgRating),
            icon = Icons.Default.Star,
            color = KoupaGold
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    val KoupaDarkSlate = Color(0xFF323E4B)
    val KoupaBackground = Color(0xFFF3F5F7)

    Column(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = KoupaBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = KoupaDarkSlate,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = KoupaDarkSlate,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun BookingBarChart(
    modifier: Modifier = Modifier
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaDarkSlate = Color(0xFF323E4B)

    // Sample data for 7 days
    val bookings = listOf(12, 8, 15, 7, 20, 10, 18)
    val maxBookings = bookings.maxOrNull() ?: 1
    val days = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")

    Column(modifier = modifier) {
        // Chart bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bookings.forEach { booking ->
                val heightFraction = booking.toFloat() / maxBookings
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(heightFraction)
                            .background(KoupaTeal, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                }
            }
        }

        // Day labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    color = KoupaDarkSlate,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TopServicesList(
    modifier: Modifier = Modifier
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaDarkSlate = Color(0xFF323E4B)
    val KoupaBackground = Color(0xFFF3F5F7)

    Column(modifier = modifier) {
        Text(
            text = "الأكثر طلبًا",
            color = KoupaDarkSlate,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Sample top services
        val services = listOf(
            Pair("قص شعر رجالي", 45),
            Pair("حلاقة لحية", 32),
            Pair("غسيل شعر", 28),
            Pair("تصفيف شعر", 22),
            Pair("عناية بالبشرة", 18)
        )

        services.forEach { (service, count) ->
            ServiceItem(
                serviceName = service,
                count = count,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun ServiceItem(
    serviceName: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val KoupaTeal = Color(0xFF1A7A78)
    val KoupaDarkSlate = Color(0xFF323E4B)
    val KoupaBackground = Color(0xFFF3F5F7)

    Row(
        modifier = modifier
            .padding(16.dp)
            .background(
                color = KoupaBackground,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = serviceName,
            color = KoupaDarkSlate,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$count حجز",
            color = KoupaTeal,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
