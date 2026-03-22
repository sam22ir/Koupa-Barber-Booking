package com.koupa.barberbooking.domain.usecase

import com.koupa.barberbooking.domain.model.Appointment
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import javax.inject.Inject

/**
 * Use case for creating an appointment.
 * Handles atomic slot booking via Supabase Edge Function.
 */
class CreateAppointmentUseCase @Inject constructor(
    private val repository: BarberShopRepository
) {
    suspend operator fun invoke(
        customerId: String,
        shopId: String,
        slotId: String
    ): Result<Appointment> {
        return repository.createAppointment(customerId, shopId, slotId)
    }
}
