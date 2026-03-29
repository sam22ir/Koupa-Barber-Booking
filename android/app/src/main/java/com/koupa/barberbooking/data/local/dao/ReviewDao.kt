package com.koupa.barberbooking.data.local.dao

import androidx.room.*
import com.koupa.barberbooking.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)
    
    @Update
    suspend fun updateReview(review: ReviewEntity)
    
    @Delete
    suspend fun deleteReview(review: ReviewEntity)
    
    @Query("SELECT * FROM reviews")
    suspend fun getAllReviews(): List<ReviewEntity>
    
    @Query("SELECT * FROM reviews")
    fun getAllReviewsFlow(): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE id = :id")
    suspend fun getReviewById(id: String): ReviewEntity?
    
    @Query("SELECT * FROM reviews WHERE shopId = :shopId")
    suspend fun getReviewsByShopId(shopId: String): List<ReviewEntity>
    
    @Query("SELECT * FROM reviews WHERE customerId = :customerId")
    suspend fun getReviewsByCustomer(customerId: String): List<ReviewEntity>
    
    @Query("SELECT AVG(rating) FROM reviews WHERE shopId = :shopId")
    suspend fun getAverageRatingForShop(shopId: String): Double?
}