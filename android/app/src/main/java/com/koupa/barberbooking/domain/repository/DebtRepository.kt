package com.koupa.barberbooking.domain.repository

import com.koupa.barberbooking.domain.model.Debt
import com.koupa.barberbooking.domain.model.DebtSummary

/**
 * Repository interface for debt operations.
 * Used by barbers to manage customer debts.
 */
interface DebtRepository {
    /**
     * Get all debts for a specific shop.
     */
    suspend fun getShopDebts(shopId: String): Result<List<Debt>>

    /**
     * Get a specific debt by ID.
     */
    suspend fun getDebtById(debtId: String): Result<Debt>

    /**
     * Create a new debt record.
     */
    suspend fun createDebt(
        shopId: String,
        customerName: String,
        amount: Int,
        notes: String?
    ): Result<Debt>

    /**
     * Update an existing debt.
     */
    suspend fun updateDebt(
        debtId: String,
        customerName: String,
        amount: Int,
        notes: String?,
        isPaid: Boolean
    ): Result<Debt>

    /**
     * Delete a debt record.
     */
    suspend fun deleteDebt(debtId: String): Result<Boolean>

    /**
     * Mark a debt as paid.
     */
    suspend fun markAsPaid(debtId: String): Result<Debt>

    /**
     * Get debt summary for a shop.
     */
    suspend fun getDebtSummary(shopId: String): Result<DebtSummary>
}
