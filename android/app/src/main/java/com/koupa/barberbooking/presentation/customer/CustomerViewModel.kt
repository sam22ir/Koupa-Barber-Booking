package com.koupa.barberbooking.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.model.Appointment
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
import java.time.LocalTime
import javax.inject.Inject

/** Determines which content is shown on the home screen. */
enum class HomeViewMode { LIST, MAP }

/**
 * Simple lat/lon pair — replaces com.google.android.gms.maps.model.LatLng.
 * Decoupled from any map SDK.
 */
data class LatLon(val latitude: Double, val longitude: Double)

/** Holds the active routing state after a successful booking. */
data class ActiveRoute(
    val shop        : BarberShop,
    val polyline    : List<LatLon> = emptyList(),
    val durationMin : Int          = 0
)

data class CustomerHomeUiState(
    val shops        : List<BarberShop>  = emptyList(),
    val isLoading    : Boolean           = false,
    val error        : String?           = null,
    // Map state
    val viewMode     : HomeViewMode      = HomeViewMode.LIST,
    val selectedShop : BarberShop?       = null,
    val userLatLon   : LatLon?           = null,
    val activeRoute  : ActiveRoute?      = null
)

data class CustomerAppointmentsUiState(
    val appointments  : List<Appointment> = emptyList(),
    val isLoading     : Boolean           = false,
    val error         : String?           = null,
    val cancelSuccess : Boolean           = false
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: BarberShopRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(CustomerHomeUiState())
    val homeState: StateFlow<CustomerHomeUiState> = _homeState.asStateFlow()

    private val _appointmentsState = MutableStateFlow(CustomerAppointmentsUiState())
    val appointmentsState: StateFlow<CustomerAppointmentsUiState> = _appointmentsState.asStateFlow()

    // ── List / Map toggle ────────────────────────────────────────────────────
    fun setViewMode(mode: HomeViewMode) = _homeState.update { it.copy(viewMode = mode) }

    // ── Marker tap → open / close bottom sheet ───────────────────────────────
    fun selectShop(shop: BarberShop?) = _homeState.update { it.copy(selectedShop = shop) }

    // ── User GPS location ─────────────────────────────────────────────────────
    fun setUserLocation(lat: Double, lon: Double) =
        _homeState.update { it.copy(userLatLon = LatLon(lat, lon)) }

    // ── Post-booking routing ──────────────────────────────────────────────────
    /**
     * Called after a booking is confirmed.
     * If the appointment starts within 60 minutes → activates the routing overlay.
     */
    fun activateRoutingIfSoon(shop: BarberShop, appointmentTimeStr: String) {
        try {
            val appointmentTime = LocalTime.parse(appointmentTimeStr)
            val minutesUntil    = java.time.Duration.between(LocalTime.now(), appointmentTime).toMinutes()
            if (minutesUntil in 0..60) {
                _homeState.update {
                    it.copy(viewMode = HomeViewMode.MAP, activeRoute = ActiveRoute(shop = shop))
                }
                val user = _homeState.value.userLatLon
                if (user != null && shop.latitude != null && shop.longitude != null) {
                    fetchOsrmRoute(user, LatLon(shop.latitude, shop.longitude))
                }
            }
        } catch (_: Exception) { }
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
                    _homeState.update { s ->
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

    /** Clears active routing (user dismissed or arrived). */
    fun clearRoute() = _homeState.update { it.copy(activeRoute = null) }

    // ── Data loading ──────────────────────────────────────────────────────────
    fun loadNearbyShops(
        latitude  : Double = 36.737232,  // Default: Algiers
        longitude : Double = 3.086472,
        radiusKm  : Double = 50.0,
        wilayaCode: Int?   = null
    ) {
        viewModelScope.launch {
            _homeState.update { it.copy(isLoading = true, error = null) }
            repository.getNearbyShops(latitude, longitude, radiusKm, wilayaCode)
                .onSuccess { shops -> _homeState.update { it.copy(isLoading = false, shops = shops) } }
                .onFailure { e   -> _homeState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun loadAppointments(customerId: String) {
        if (customerId.isBlank()) return
        viewModelScope.launch {
            _appointmentsState.update { it.copy(isLoading = true, error = null) }
            repository.getCustomerAppointments(customerId)
                .onSuccess { appts -> _appointmentsState.update { it.copy(isLoading = false, appointments = appts) } }
                .onFailure { e     -> _appointmentsState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun cancelAppointment(appointmentId: String, userId: String) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId, userId)
                .onSuccess {
                    _appointmentsState.update {
                        it.copy(
                            cancelSuccess = true,
                            appointments  = it.appointments.filterNot { a -> a.id == appointmentId }
                        )
                    }
                }
                .onFailure { e -> _appointmentsState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _homeState.update { it.copy(error = null) }
        _appointmentsState.update { it.copy(error = null) }
    }
}
