package com.koupa.barberbooking.data.repository

import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import com.koupa.barberbooking.data.mapper.ReviewMapper
import com.koupa.barberbooking.domain.model.Review
import com.koupa.barberbooking.domain.repository.ReviewRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ReviewRepository.
 * Uses Supabase Postgrest for review operations.
 */
@Singleton
class ReviewRepositoryImpl @Inject constructor() : ReviewRepository {

    private val supabase = SupabaseClientFactory.client

    override suspend fun getShopReviews(shopId: String): Result<List<Review>> {
        return try {
            // Join with users table to get customer name
            val result = supabase.from("reviews")
                .select {
                    filter { eq("shop_id", shopId) }
                    // Order by most recent first
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<JsonObject>()

            val reviews = result.map { json ->
                // Get customer name from users table
                val customerId = json["customer_id"]?.jsonPrimitive?.content ?: ""
                val customerName = getCustomerName(customerId)
                // Create review with customerName set
                ReviewMapper.fromJson(json).copy(customerName = customerName)
            }

            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviewById(reviewId: String): Result<Review> {
        return try {
            val result = supabase.from("reviews")
                .select {
                    filter { eq("id", reviewId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Review not found"))

            val review = ReviewMapper.fromJson(result)
            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReview(
        shopId: String,
        customerId: String,
        rating: Int,
        comment: String?
    ): Result<Review> {
        return try {
            val insertData = buildJsonObject {
                put("shop_id", JsonPrimitive(shopId))
                put("customer_id", JsonPrimitive(customerId))
                put("rating", JsonPrimitive(rating))
                comment?.let { put("comment", JsonPrimitive(it)) }
            }

            supabase.from("reviews").insert(insertData)

            // Fetch the created review
            val result = supabase.from("reviews")
                .select {
                    filter {
                        eq("shop_id", shopId)
                        eq("customer_id", customerId)
                    }
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    limit(1)
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Failed to fetch created review"))

            val review = ReviewMapper.fromJson(result)
            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReview(
        reviewId: String,
        rating: Int,
        comment: String?
    ): Result<Review> {
        return try {
            val updates = ReviewMapper.toUpdateJson(rating, comment)
            supabase.from("reviews")
                .update(updates) { filter { eq("id", reviewId) } }

            // Fetch updated review
            val result = supabase.from("reviews")
                .select {
                    filter { eq("id", reviewId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Review not found"))

            val review = ReviewMapper.fromJson(result)
            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: String, userId: String): Result<Boolean> {
        return try {
            // First verify the user owns this review
            val result = supabase.from("reviews")
                .select {
                    filter { eq("id", reviewId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Review not found"))

            val reviewCustomerId = result["customer_id"]?.jsonPrimitive?.content
            if (reviewCustomerId != userId) {
                return Result.failure(Exception("Not authorized to delete this review"))
            }

            supabase.from("reviews")
                .delete { filter { eq("id", reviewId) } }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasUserReviewed(shopId: String, customerId: String): Result<Boolean> {
        return try {
            val result = supabase.from("reviews")
                .select {
                    filter {
                        eq("shop_id", shopId)
                        eq("customer_id", customerId)
                    }
                }
                .decodeList<JsonObject>()

            Result.success(result.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShopRatingSummary(shopId: String): Result<Pair<Double, Int>> {
        return try {
            // Get from the barbershops table (cached by trigger)
            val result = supabase.from("barbershops")
                .select {
                    filter { eq("id", shopId) }
                    select(
                        io.github.jan.supabase.postgrest.query.Columns.raw("average_rating, review_count")
                    )
                }
                .decodeList<JsonObject>()
                .firstOrNull()

            val avgRating = result?.get("average_rating")?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
            val count = result?.get("review_count")?.jsonPrimitive?.content?.toIntOrNull() ?: 0

            Result.success(Pair(avgRating, count))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCustomerName(customerId: String): String? {
        return try {
            val result = supabase.from("users")
                .select {
                    filter { eq("id", customerId) }
                    select(io.github.jan.supabase.postgrest.query.Columns.raw("full_name"))
                }
                .decodeList<JsonObject>()
                .firstOrNull()

            result?.get("full_name")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }
}