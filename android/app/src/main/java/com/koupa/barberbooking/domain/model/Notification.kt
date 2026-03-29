package com.koupa.barberbooking.domain.model

import java.time.ZonedDateTime

/**
 * Domain model for Notification entity.
 * Maps to Supabase 'notifications' table.
 */
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val appointmentId: String? = null,
    val createdAt: ZonedDateTime
)

enum class NotificationType {
    BOOKING, CANCEL, REMINDER, SYSTEM;

    companion object {
        fun fromString(value: String): NotificationType = when (value.lowercase()) {
            "booking" -> BOOKING
            "cancel" -> CANCEL
            "reminder" -> REMINDER
            "system" -> SYSTEM
            else -> SYSTEM // Default to SYSTEM for unknown types
        }
    }

    fun toApiString(): String = name.lowercase()
}
