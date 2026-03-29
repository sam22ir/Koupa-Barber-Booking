package com.koupa.barberbooking.domain.model

/**
 * Domain model for Debt entity.
 * Maps to Supabase 'debts' table.
 * Used by barbers to track customer debts.
 */
data class Debt(
    val id: String = "",
    val shopId: String,
    val customerName: String,
    val amount: Int,
    val notes: String? = null,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Summary of debts for a shop.
 */
data class DebtSummary(
    val totalDebt: Int,
    val unpaidCount: Int,
    val paidCount: Int
)
