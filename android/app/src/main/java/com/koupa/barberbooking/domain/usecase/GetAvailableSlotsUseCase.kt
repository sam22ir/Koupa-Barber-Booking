package com.koupa.barberbooking.domain.usecase

import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case for fetching available time slots for a shop on a given date.
 */
class GetAvailableSlotsUseCase @Inject constructor(
    private val repository: BarberShopRepository
) {
    suspend operator fun invoke(
        shopId: String,
        date: LocalDate
    ): Result<List<AvailabilitySlot>> {
        return repository.getAvailableSlots(shopId, date)
    }
}
