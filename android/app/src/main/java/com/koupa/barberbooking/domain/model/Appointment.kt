package com.koupa.barberbooking.domain.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model for Appointment entity.
 * Maps to Supabase 'appointments' table.
 */
data class Appointment(
    val id: String,
    val customerId: String,
    val shopId: String,
    val slotId: String,
    val appointmentDate: LocalDate,
    val timeSlot: LocalTime,
    val status: AppointmentStatus,
    val paymentMethod: String = "cash_on_arrival"
)

enum class AppointmentStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED;

    companion object {
        fun fromString(value: String): AppointmentStatus = when (value.lowercase()) {
            "pending" -> PENDING
            "confirmed" -> CONFIRMED
            "cancelled" -> CANCELLED
            "completed" -> COMPLETED
            else -> throw IllegalArgumentException("Invalid status: $value")
        }
    }

    fun toApiString(): String = name.lowercase()
}
