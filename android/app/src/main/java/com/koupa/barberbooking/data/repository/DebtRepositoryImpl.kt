package com.koupa.barberbooking.data.repository

import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import com.koupa.barberbooking.data.mapper.DebtMapper
import com.koupa.barberbooking.domain.model.Debt
import com.koupa.barberbooking.domain.model.DebtSummary
import com.koupa.barberbooking.domain.repository.DebtRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DebtRepository.
 * Uses Supabase Postgrest for debt operations.
 */
@Singleton
class DebtRepositoryImpl @Inject constructor() : DebtRepository {

    private val supabase = SupabaseClientFactory.client

    override suspend fun getShopDebts(shopId: String): Result<List<Debt>> {
        return try {
            val result = supabase.from("debts")
                .select {
                    filter { eq("shop_id", shopId) }
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<JsonObject>()

            val debts = result.map { DebtMapper.fromJson(it) }
            Result.success(debts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDebtById(debtId: String): Result<Debt> {
        return try {
            val result = supabase.from("debts")
                .select {
                    filter { eq("id", debtId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Debt not found"))

            Result.success(DebtMapper.fromJson(result))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createDebt(
        shopId: String,
        customerName: String,
        amount: Int,
        notes: String?
    ): Result<Debt> {
        return try {
            val insertData = DebtMapper.toInsertJson(shopId, customerName, amount, notes)
            supabase.from("debts").insert(insertData)

            // Fetch the created debt
            val result = supabase.from("debts")
                .select {
                    filter { eq("shop_id", shopId) }
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Failed to fetch created debt"))

            Result.success(DebtMapper.fromJson(result))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDebt(
        debtId: String,
        customerName: String,
        amount: Int,
        notes: String?,
        isPaid: Boolean
    ): Result<Debt> {
        return try {
            val updates = DebtMapper.toUpdateJson(customerName, amount, notes, isPaid)
            supabase.from("debts")
                .update(updates) { filter { eq("id", debtId) } }

            // Fetch updated debt
            val result = supabase.from("debts")
                .select {
                    filter { eq("id", debtId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Debt not found"))

            Result.success(DebtMapper.fromJson(result))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDebt(debtId: String): Result<Boolean> {
        return try {
            supabase.from("debts")
                .delete { filter { eq("id", debtId) } }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsPaid(debtId: String): Result<Debt> {
        return try {
            supabase.from("debts")
                .update(
                    kotlinx.serialization.json.buildJsonObject {
                        put("is_paid", kotlinx.serialization.json.JsonPrimitive(true))
                    }
                ) { filter { eq("id", debtId) } }

            // Fetch updated debt
            val result = supabase.from("debts")
                .select {
                    filter { eq("id", debtId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Debt not found"))

            Result.success(DebtMapper.fromJson(result))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDebtSummary(shopId: String): Result<DebtSummary> {
        return try {
            val result = supabase.from("debts")
                .select {
                    filter { eq("shop_id", shopId) }
                }
                .decodeList<JsonObject>()

            val debts = result.map { DebtMapper.fromJson(it) }
            val unpaidDebts = debts.filter { !it.isPaid }
            val paidDebts = debts.filter { it.isPaid }

            val totalDebt = unpaidDebts.sumOf { it.amount }

            Result.success(
                DebtSummary(
                    totalDebt = totalDebt,
                    unpaidCount = unpaidDebts.size,
                    paidCount = paidDebts.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
