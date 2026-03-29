package com.koupa.barberbooking.domain.model

/**
 * Domain model for Review entity.
 * Maps to Supabase 'reviews' table.
 */
data class Review(
    val id: String = "",
    val shopId: String,
    val customerId: String,
    val customerName: String? = null,  // Cached from users table
    val rating: Int,                     // 1-5 stars
    val comment: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Rating summary for a barbershop.
 */
data class RatingSummary(
    val averageRating: Double,
    val reviewCount: Int
)