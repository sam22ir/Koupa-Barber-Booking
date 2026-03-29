package com.koupa.barberbooking.data.local.dao

import androidx.room.*
import com.koupa.barberbooking.data.local.entity.BarberShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BarberShopDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarberShop(shop: BarberShopEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shops: List<BarberShopEntity>)
    
    @Update
    suspend fun updateBarberShop(shop: BarberShopEntity)
    
    @Delete
    suspend fun deleteBarberShop(shop: BarberShopEntity)
    
    @Query("SELECT * FROM barber_shops")
    suspend fun getAllBarberShops(): List<BarberShopEntity>
    
    @Query("SELECT * FROM barber_shops")
    fun getAllBarberShopsFlow(): Flow<List<BarberShopEntity>>
    
    @Query("SELECT * FROM barber_shops WHERE id = :id")
    suspend fun getBarberShopById(id: String): BarberShopEntity?
    
    @Query("SELECT * FROM barber_shops WHERE isShopOpen = 1")
    suspend fun getOpenShops(): List<BarberShopEntity>
    
    @Query("SELECT * FROM barber_shops ORDER BY rating DESC LIMIT :limit")
    suspend fun getTopRatedShops(limit: Int): List<BarberShopEntity>
    
    @Query("SELECT * FROM barber_shops WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun getShopsInArea(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<BarberShopEntity>
    
    @Query("SELECT * FROM barber_shops ORDER BY (latitude - :userLat)*(latitude - :userLat) + (longitude - :userLng)*(longitude - :userLng) ASC LIMIT :limit")
    suspend fun getNearestShops(userLat: Double, userLng: Double, limit: Int): List<BarberShopEntity>
}