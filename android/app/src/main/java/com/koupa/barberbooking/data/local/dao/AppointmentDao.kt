package com.koupa.barberbooking.data.local.dao

import androidx.room.*
import com.koupa.barberbooking.data.local.entity.AppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appointments: List<AppointmentEntity>)
    
    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)
    
    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)
    
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<AppointmentEntity>
    
    @Query("SELECT * FROM appointments")
    fun getAllAppointmentsFlow(): Flow<List<AppointmentEntity>>
    
    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: String): AppointmentEntity?
    
    @Query("SELECT * FROM appointments WHERE customerId = :customerId")
    suspend fun getAppointmentsByCustomer(customerId: String): List<AppointmentEntity>
    
    @Query("SELECT * FROM appointments WHERE shopId = :shopId")
    suspend fun getAppointmentsByShop(shopId: String): List<AppointmentEntity>
    
    @Query("SELECT * FROM appointments WHERE status = :status")
    suspend fun getAppointmentsByStatus(status: String): List<AppointmentEntity>
    
    @Query("SELECT * FROM appointments WHERE appointmentDate = :date")
    suspend fun getAppointmentsByDate(date: String): List<AppointmentEntity>
}