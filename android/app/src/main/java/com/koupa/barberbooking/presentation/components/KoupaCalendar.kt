package com.koupa.barberbooking.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.koupa.barberbooking.ui.theme.KoupaAnimationTokens
import com.koupa.barberbooking.ui.theme.KoupaShapes
import com.koupa.barberbooking.ui.theme.KoupaSpacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Data class representing a date in the calendar
 */
data class CalendarDate(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isAvailable: Boolean = true,
    val hasEvent: Boolean = false
)

/**
 * Koupa Calendar Component
 * Material 3 style calendar with tonal elevation and smooth animations
 */
@Composable
fun KoupaCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate = LocalDate.now(),
    maxDate: LocalDate = LocalDate.now().plusMonths(3),
    availableDates: Set<LocalDate> = emptySet(),
    eventsDates: Set<LocalDate> = emptySet()
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val colorScheme = MaterialTheme.colorScheme

Card(
            modifier = modifier.fillMaxWidth(),
            shape = KoupaShapes.Large,
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = KoupaSpacing.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KoupaSpacing.md)
        ) {
            // Header with month/year and navigation
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    val newMonth = currentMonth.minusMonths(1)
                    if (newMonth.atDay(1).isAfter(minDate.minusDays(1))) {
                        currentMonth = newMonth
                    }
                },
                onNextMonth = {
                    val newMonth = currentMonth.plusMonths(1)
                    if (newMonth.atDay(1).isBefore(maxDate.plusDays(1))) {
                        currentMonth = newMonth
                    }
                },
                canGoPrevious = currentMonth.atDay(1).isAfter(minDate.minusDays(1)),
                canGoNext = currentMonth.atDay(1).isBefore(maxDate.plusDays(1))
            )

            Spacer(modifier = Modifier.height(KoupaSpacing.md))

            // Days of week header
            DaysOfWeekHeader()

            Spacer(modifier = Modifier.height(KoupaSpacing.sm))

            // Calendar grid
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                minDate = minDate,
                maxDate = maxDate,
                availableDates = availableDates,
                eventsDates = eventsDates,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous month button
        IconButton(
            onClick = onPreviousMonth,
            enabled = canGoPrevious
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "الشهر السابق",
                tint = if (canGoPrevious) colorScheme.onSurface else colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
        }

        // Month and year
        Text(
            text = currentMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar"))
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        // Next month button
        IconButton(
            onClick = onNextMonth,
            enabled = canGoNext
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "الشهر التالي",
                tint = if (canGoNext) colorScheme.onSurface else colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day.take(2), // First two letters
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    availableDates: Set<LocalDate>,
    eventsDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()

    // Calculate days to show from previous month
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysFromPreviousMonth = firstDayOfWeek

    // Calculate total days to display (6 weeks * 7 days = 42)
    val totalDays = 42
    val days = mutableListOf<CalendarDate>()

    // Add days from previous month
    val previousMonth = currentMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()
    for (i in daysFromPreviousMonth - 1 downTo 0) {
        val date = previousMonth.atDay(daysInPreviousMonth - i)
        days.add(
            CalendarDate(
                date = date,
                isCurrentMonth = false,
                isToday = date.isEqual(LocalDate.now()),
                isSelected = date.isEqual(selectedDate),
                isAvailable = date in availableDates && !date.isBefore(minDate) && !date.isAfter(maxDate)
            )
        )
    }

    // Add days from current month
    for (day in 1..currentMonth.lengthOfMonth()) {
        val date = currentMonth.atDay(day)
        days.add(
            CalendarDate(
                date = date,
                isCurrentMonth = true,
                isToday = date.isEqual(LocalDate.now()),
                isSelected = date.isEqual(selectedDate),
                isAvailable = date in availableDates && !date.isBefore(minDate) && !date.isAfter(maxDate),
                hasEvent = date in eventsDates
            )
        )
    }

    // Add days from next month to fill the grid
    val remainingDays = totalDays - days.size
    val nextMonth = currentMonth.plusMonths(1)
    for (day in 1..remainingDays) {
        val date = nextMonth.atDay(day)
        days.add(
            CalendarDate(
                date = date,
                isCurrentMonth = false,
                isToday = date.isEqual(LocalDate.now()),
                isSelected = date.isEqual(selectedDate),
                isAvailable = date in availableDates && !date.isBefore(minDate) && !date.isAfter(maxDate)
            )
        )
    }

    // Display 6 rows of 7 days
    Column {
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0 until 7) {
                    val index = week * 7 + dayOfWeek
                    if (index < days.size) {
                        CalendarDayCell(
                            calendarDate = days[index],
                            onClick = { onDateSelected(days[index].date) }
                        )
                    }
                }
            }
            if (week < 5) {
                Spacer(modifier = Modifier.height(KoupaSpacing.xs))
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    calendarDate: CalendarDate,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val backgroundColor by animateColorAsState(
        targetValue = when {
            calendarDate.isSelected -> colorScheme.primary
            calendarDate.isToday -> colorScheme.primaryContainer
            else -> colorScheme.surface
        },
        animationSpec = tween(KoupaAnimationTokens.DURATION_MEDIUM),
        label = "day_background"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            calendarDate.isSelected -> colorScheme.onPrimary
            calendarDate.isToday -> colorScheme.onPrimaryContainer
            !calendarDate.isCurrentMonth -> colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            !calendarDate.isAvailable -> colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            else -> colorScheme.onSurface
        },
        animationSpec = tween(KoupaAnimationTokens.DURATION_MEDIUM),
        label = "day_content"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                enabled = calendarDate.isAvailable && calendarDate.isCurrentMonth,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = calendarDate.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (calendarDate.isSelected || calendarDate.isToday) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )

        // Event indicator dot
        if (calendarDate.hasEvent && !calendarDate.isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary)
            )
        }
    }
}

/**
 * Compact Calendar for horizontal scrolling
 */
@Composable
fun KoupaCompactCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    daysToShow: Int = 14,
    availableDates: Set<LocalDate> = emptySet()
) {
    val startDate = LocalDate.now()
    val dates = (0 until daysToShow).map { startDate.plusDays(it.toLong()) }
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(KoupaSpacing.sm)
    ) {
        dates.forEach { date ->
            val isSelected = date.isEqual(selectedDate)
            val isToday = date.isEqual(LocalDate.now())
            val isAvailable = date in availableDates

            val backgroundColor by animateColorAsState(
                targetValue = when {
                    isSelected -> colorScheme.primary
                    isToday -> colorScheme.primaryContainer
                    else -> colorScheme.surface
                },
                animationSpec = tween(KoupaAnimationTokens.DURATION_MEDIUM),
                label = "compact_day_bg"
            )

            val contentColor by animateColorAsState(
                targetValue = when {
                    isSelected -> colorScheme.onPrimary
                    isToday -> colorScheme.onPrimaryContainer
                    !isAvailable -> colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    else -> colorScheme.onSurface
                },
                animationSpec = tween(KoupaAnimationTokens.DURATION_MEDIUM),
                label = "compact_day_content"
            )

Card(
                    modifier = Modifier
                        .width(64.dp)
                        .height(72.dp)
                        .clickable(enabled = isAvailable) { onDateSelected(date) },
                    shape = KoupaShapes.Medium,
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) KoupaSpacing.xs else 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Day name
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ar")),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(KoupaSpacing.xs))

                    // Day number
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}
