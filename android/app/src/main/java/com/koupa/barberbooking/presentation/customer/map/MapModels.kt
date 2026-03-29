package com.koupa.barberbooking.presentation.customer.map

/**
 * Simple lat/lon pair — decoupled from any map SDK.
 */
data class LatLon(val latitude: Double, val longitude: Double)

/**
 * Holds the active routing state after a successful booking.
 */
data class ActiveRoute(
    val shop: com.koupa.barberbooking.domain.model.BarberShop,
    val polyline: List<LatLon> = emptyList(),
    val durationMin: Int = 0
)