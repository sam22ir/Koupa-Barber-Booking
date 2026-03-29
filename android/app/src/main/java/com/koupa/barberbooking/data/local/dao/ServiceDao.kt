package com.koupa.barberbooking.data.local.dao

import androidx.room.*
import com.koupa.barberbooking.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)
    
    @Update
    suspend fun updateService(service: ServiceEntity)
    
    @Delete
    suspend fun deleteService(service: ServiceEntity)
    
    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>
    
    @Query("SELECT * FROM services")
    fun getAllServicesFlow(): Flow<List<ServiceEntity>>
    
    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: String): ServiceEntity?
    
    @Query("SELECT * FROM services WHERE shopId = :shopId")
    suspend fun getServicesByShopId(shopId: String): List<ServiceEntity>
    
    @Query("SELECT * FROM services WHERE name LIKE :name")
    suspend fun searchServicesByName(name: String): List<ServiceEntity>
}