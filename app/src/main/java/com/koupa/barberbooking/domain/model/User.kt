package com.koupa.barberbooking.domain.model

import java.time.Instant

/**
 * Domain model for User entity.
 * Maps to Supabase 'users' table.
 */
data class User(
    val id: String,
    val phoneNumber: String,
    val fullName: String?,
    val role: UserRole,
    val fcmToken: String?,
    val language: String,
    val createdAt: Instant
)

enum class UserRole {
    CUSTOMER, BARBER;

    companion object {
        fun fromString(value: String): UserRole = when (value.lowercase()) {
            "customer" -> CUSTOMER
            "barber" -> BARBER
            else -> throw IllegalArgumentException("Invalid role: $value")
        }
    }
}
