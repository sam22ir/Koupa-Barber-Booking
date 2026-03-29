package com.koupa.barberbooking.domain.repository

import com.koupa.barberbooking.domain.model.Review

/**
 * Repository interface for review operations.
 */
interface ReviewRepository {
    /**
     * Get all reviews for a specific barbershop.
     */
    suspend fun getShopReviews(shopId: String): Result<List<Review>>

    /**
     * Get a specific review by ID.
     */
    suspend fun getReviewById(reviewId: String): Result<Review>

    /**
     * Create a new review for a barbershop.
     * Only customers can create reviews.
     */
    suspend fun createReview(
        shopId: String,
        customerId: String,
        rating: Int,
        comment: String?
    ): Result<Review>

    /**
     * Update an existing review.
     * Only the original reviewer can update their review.
     */
    suspend fun updateReview(
        reviewId: String,
        rating: Int,
        comment: String?
    ): Result<Review>

    /**
     * Delete a review.
     * Only the original reviewer can delete their review.
     */
    suspend fun deleteReview(reviewId: String, userId: String): Result<Boolean>

    /**
     * Check if a customer has already reviewed a shop.
     */
    suspend fun hasUserReviewed(shopId: String, customerId: String): Result<Boolean>

    /**
     * Get the rating summary for a barbershop.
     */
    suspend fun getShopRatingSummary(shopId: String): Result<Pair<Double, Int>>
}