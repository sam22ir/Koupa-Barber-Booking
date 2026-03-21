package com.koupa.barberbooking.domain.usecase

import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import javax.inject.Inject

/**
 * Use case for finding nearby barbershops.
 * Uses PostGIS spatial queries via Supabase Edge Function.
 */
class GetNearbyShopsUseCase @Inject constructor(
    private val repository: BarberShopRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        wilayaCode: Int? = null
    ): Result<List<BarberShop>> {
        return repository.getNearbyShops(latitude, longitude, radiusKm, wilayaCode)
    }
}
