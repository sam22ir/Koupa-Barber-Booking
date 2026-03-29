package com.koupa.barberbooking.domain.repository

import com.koupa.barberbooking.domain.model.Appointment
import com.koupa.barberbooking.domain.model.AppointmentStatus
import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.model.BarberShop
import java.time.LocalDate

/**
 * Repository interface for barbershop and appointment operations.
 */
interface BarberShopRepository {
    // Barbershop operations
    suspend fun getNearbyShops(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        wilayaCode: Int? = null
    ): Result<List<BarberShop>>

    suspend fun getShopById(shopId: String): Result<BarberShop>

    suspend fun createShop(shop: BarberShop): Result<BarberShop>

    suspend fun updateShop(shop: BarberShop): Result<BarberShop>

    // Availability slot operations
    suspend fun getAvailableSlots(shopId: String, date: LocalDate): Result<List<AvailabilitySlot>>

    suspend fun createSlots(shopId: String, date: LocalDate, times: List<String>): Result<List<AvailabilitySlot>>

    suspend fun toggleSlot(slotId: String, isOpen: Boolean): Result<AvailabilitySlot>

    // Appointment operations
    suspend fun createAppointment(
        customerId: String,
        shopId: String,
        slotId: String
    ): Result<Appointment>

    suspend fun cancelAppointment(appointmentId: String, userId: String): Result<Boolean>

    suspend fun updateAppointmentStatus(
        appointmentId: String,
        status: AppointmentStatus
    ): Result<Appointment>

    suspend fun getCustomerAppointments(customerId: String): Result<List<Appointment>>

    suspend fun getShopAppointments(shopId: String, date: LocalDate): Result<List<Appointment>>
    
    suspend fun getShopByOwnerId(ownerId: String): Result<BarberShop?>
}
