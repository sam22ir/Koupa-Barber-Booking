package com.koupa.barberbooking.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model for AvailabilitySlot entity.
 * Maps to Supabase 'availability_slots' table.
 */
data class AvailabilitySlot(
    val id: String,
    val shopId: String,
    val slotDate: LocalDate,
    val slotTime: LocalTime,
    val isOpen: Boolean,
    val isBooked: Boolean
)
