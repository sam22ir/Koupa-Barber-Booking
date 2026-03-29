package com.koupa.barberbooking.presentation.customer.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * Bottom sheet expansion states for the immersive map screen.
 * A = collapsed (shop name only)
 * B = half-expanded (shop details)
 * C = fully expanded (booking calendar)
 */
enum class BottomSheetState { A, B, C }

/**
 * Complete UI state for the immersive map screen.
 */
data class CustomerMapUiState(
    // Loading & error
    val isLoading    : Boolean           = false,
    val error        : String?           = null,
    
    // Map data
    val nearbyShops  : List<BarberShop>  = emptyList(),
    val userLocation: LatLon?           = null,
    
    // Selected shop & bottom sheet
    val selectedShop   : BarberShop?       = null,
    val bottomSheetState: BottomSheetState = BottomSheetState.A,
    
    // Active routing
    val activeRoute  : ActiveRoute?      = null,
    
    // Booking state
    val selectedDate    : java.time.LocalDate?             = null,
    val selectedTime    : java.time.LocalTime?             = null,
    val availableSlots  : List<AvailabilitySlot> = emptyList(),
    val isLoadingSlots  : Boolean                = false,
    val bookingSuccess  : Boolean                = false,
    val bookingError    : String?                = null
)

@HiltViewModel
class CustomerMapViewModel @Inject constructor(
    private val repository: BarberShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerMapUiState())
    val uiState: StateFlow<CustomerMapUiState> = _uiState.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────────
    // Location & Map State
    // ─────────────────────────────────────────────────────────────────────────────
    
    /**
     * Updates the user's current GPS location.
     */
    fun setUserLocation(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(userLocation = LatLon(latitude, longitude)) }
    }

    /**
     * Updates nearby shops list.
     */
    fun loadNearbyShops(
        latitude  : Double = 36.737232,  // Default: Algiers
        longitude : Double = 3.086472,
        radiusKm  : Double = 50.0,
        wilayaCode: Int?   = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getNearbyShops(latitude, longitude, radiusKm, wilayaCode)
                .onSuccess { shops -> 
                    _uiState.update { it.copy(isLoading = false, nearbyShops = shops) } 
                }
                .onFailure { e -> 
                    _uiState.update { it.copy(isLoading = false, error = e.message) } 
                }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Shop Selection & Bottom Sheet
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Selects a shop and opens the bottom sheet at state A.
     */
    fun selectShop(shop: BarberShop?) {
        _uiState.update { 
            it.copy(
                selectedShop = shop,
                bottomSheetState = if (shop != null) BottomSheetState.A else BottomSheetState.A,
                // Reset booking state when selecting new shop
                selectedDate = null,
                selectedTime = null,
                availableSlots = emptyList(),
                bookingSuccess = false,
                bookingError = null
            ) 
        }
        
        // Auto-load slots if a shop is selected
        if (shop != null && _uiState.value.selectedDate != null) {
            loadAvailableSlots(shop.id, _uiState.value.selectedDate!!)
        }
    }

    /**
     * Transitions bottom sheet to the next state (A → B → C → A).
     */
    fun advanceBottomSheetState() {
        _uiState.update { state ->
            val nextState = when (state.bottomSheetState) {
                BottomSheetState.A -> BottomSheetState.B
                BottomSheetState.B -> BottomSheetState.C
                BottomSheetState.C -> BottomSheetState.A
            }
            state.copy(bottomSheetState = nextState)
        }
    }

    /**
     * Sets bottom sheet to a specific state.
     */
    fun setBottomSheetState(state: BottomSheetState) {
        _uiState.update { it.copy(bottomSheetState = state) }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Booking State
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Selects a date and loads available slots.
     */
    fun selectDate(date: LocalDate) {
        _uiState.update { 
            it.copy(
                selectedDate = date,
                selectedTime = null,
                availableSlots = emptyList()
            ) 
        }
        
        // Load slots for selected shop and date
        val shop = _uiState.value.selectedShop
        if (shop != null) {
            loadAvailableSlots(shop.id, date)
        }
    }

    /**
     * Selects a time slot.
     */
    fun selectTime(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    /**
     * Loads available slots for a shop on a given date.
     */
    private fun loadAvailableSlots(shopId: String, date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSlots = true) }
            repository.getAvailableSlots(shopId, date)
                .onSuccess { slots ->
                    _uiState.update { it.copy(isLoadingSlots = false, availableSlots = slots) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingSlots = false, error = e.message) }
                }
        }
    }

    /**
     * Creates a booking for the selected shop, date, and time.
     */
    fun createBooking(customerId: String) {
        val shop = _uiState.value.selectedShop ?: return
        val date = _uiState.value.selectedDate ?: return
        val time = _uiState.value.selectedTime ?: return
        
        // Find the matching slot
        val slot = _uiState.value.availableSlots.find { 
            it.slotDate == date && it.slotTime == time && it.isOpen && !it.isBooked 
        } ?: run {
            _uiState.update { it.copy(bookingError = "Slot not available") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, bookingError = null) }
            repository.createAppointment(customerId, shop.id, slot.id)
                .onSuccess { _ ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            bookingSuccess = true
                        ) 
                    }
                    // Optionally activate routing if appointment is soon
                    activateRoutingIfSoon(shop, time.toString())
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            bookingError = e.message ?: "Booking failed"
                        ) 
                    }
                }
        }
    }

    /**
     * Clears booking success flag (e.g., after navigating away).
     */
    fun clearBookingSuccess() {
        _uiState.update { it.copy(bookingSuccess = false) }
    }

    /**
     * Clears booking error.
     */
    fun clearBookingError() {
        _uiState.update { it.copy(bookingError = null) }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Routing (OSRM)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Activates routing when a shop is selected (pre-booking).
     * Sets up the ActiveRoute and fetches the OSRM polyline.
     */
    fun activateRoutingForSelection(shop: BarberShop, destination: LatLon) {
        // Only activate if not already showing this shop's route
        if (_uiState.value.activeRoute?.shop?.id != shop.id) {
            _uiState.update {
                it.copy(activeRoute = ActiveRoute(shop = shop))
            }
            val user = _uiState.value.userLocation
            if (user != null) {
                fetchOsrmRoute(user, destination)
            }
        }
    }

    /**
     * Called after a booking is confirmed.
     * If the appointment starts within 60 minutes → activates the routing overlay.
     */
    fun activateRoutingIfSoon(shop: BarberShop, appointmentTimeStr: String) {
        try {
            val appointmentTime = LocalTime.parse(appointmentTimeStr)
            val minutesUntil    = java.time.Duration.between(LocalTime.now(), appointmentTime).toMinutes()
            if (minutesUntil in 0..60) {
                _uiState.update {
                    it.copy(activeRoute = ActiveRoute(shop = shop))
                }
                val user = _uiState.value.userLocation
                if (user != null && shop.latitude != null && shop.longitude != null) {
                    fetchOsrmRoute(user, LatLon(shop.latitude, shop.longitude))
                }
            }
        } catch (_: Exception) { /* Ignore time parsing errors */ }
    }

    /**
     * Fetches a driving route from the free public OSRM API.
     * Endpoint: http://router.project-osrm.org/route/v1/driving/{lon},{lat};{lon},{lat}?geometries=geojson
     * The response geometry uses GeoJSON [lon, lat] coordinate order.
     */
    fun fetchOsrmRoute(origin: LatLon, destination: LatLon) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url =
                    "http://router.project-osrm.org/route/v1/driving/" +
                    "${origin.longitude},${origin.latitude};" +
                    "${destination.longitude},${destination.latitude}" +
                    "?geometries=geojson&overview=full"

                val json   = JSONObject(URL(url).readText())
                val routes = json.getJSONArray("routes")
                if (routes.length() == 0) return@launch

                val route       = routes.getJSONObject(0)
                val durationMin = (route.getDouble("duration") / 60.0).toInt()

                // GeoJSON LineString — coordinates are [lon, lat]
                val coords  = route.getJSONObject("geometry").getJSONArray("coordinates")
                val polyline = (0 until coords.length()).map { i ->
                    val pt  = coords.getJSONArray(i)
                    LatLon(pt.getDouble(1), pt.getDouble(0)) // flip [lon,lat] → LatLon(lat,lon)
                }

                withContext(Dispatchers.Main) {
                    _uiState.update { s ->
                        s.copy(
                            activeRoute = s.activeRoute?.copy(
                                polyline    = polyline,
                                durationMin = durationMin
                            )
                        )
                    }
                }
            } catch (_: Exception) { /* Network failure — silently ignore */ }
        }
    }

    /**
     * Clears active routing (user dismissed or arrived).
     */
    fun clearRoute() {
        _uiState.update { it.copy(activeRoute = null) }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Error Handling
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Clears general error state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Resets the entire state (e.g., when leaving the map screen).
     */
    fun resetState() {
        _uiState.value = CustomerMapUiState()
    }
}
