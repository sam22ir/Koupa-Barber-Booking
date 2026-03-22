package com.koupa.barberbooking.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for barber shop creation and editing.
 * Handles both the onboarding flow (create) and the settings flow (edit).
 */
@HiltViewModel
class CreateShopViewModel @Inject constructor(
    private val barberShopRepository: BarberShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateShopUiState())
    val uiState: StateFlow<CreateShopUiState> = _uiState.asStateFlow()

    // ── Step 1 fields ──────────────────────────────────────────────────────────
    fun onShopNameChanged(v: String)     = _uiState.update { it.copy(shopName = v, error = null) }
    fun onWilayaCodeChanged(v: Int?)     = _uiState.update { it.copy(wilayaCode = v, error = null) }
    fun onCityChanged(v: String)         = _uiState.update { it.copy(city = v, error = null) }
    fun onAddressChanged(v: String)      = _uiState.update { it.copy(address = v) }
    fun onWhatsappChanged(v: String)     = _uiState.update { it.copy(whatsappNumber = v) }

    // ── Step 2 fields ──────────────────────────────────────────────────────────
    fun onServiceToggled(service: String) {
        _uiState.update { state ->
            val current = state.services.toMutableList()
            if (current.contains(service)) current.remove(service) else current.add(service)
            state.copy(services = current)
        }
    }
    fun onOpeningFromChanged(v: String)  = _uiState.update { it.copy(openingFrom = v) }
    fun onOpeningToChanged(v: String)    = _uiState.update { it.copy(openingTo = v) }
    fun onPriceMinChanged(v: Int)        = _uiState.update { it.copy(priceMin = v) }
    fun onPriceMaxChanged(v: Int)        = _uiState.update { it.copy(priceMax = v) }

    // ── Load existing shop for Edit mode ───────────────────────────────────────
    fun loadShopForEdit(shop: BarberShop) {
        _uiState.update {
            it.copy(
                shopId        = shop.id,
                ownerId       = shop.ownerId,
                shopName      = shop.shopName,
                wilayaCode    = shop.wilayaCode,
                city          = shop.city,
                address       = shop.address ?: "",
                whatsappNumber = shop.whatsappNumber ?: "",
                services      = shop.services.toMutableList(),
                openingFrom   = shop.openingFrom,
                openingTo     = shop.openingTo,
                priceMin      = shop.priceMin,
                priceMax      = shop.priceMax,
                isEditMode    = true
            )
        }
    }

    // ── Validate Step 1 ────────────────────────────────────────────────────────
    fun validateStep1(): Boolean {
        val s = _uiState.value
        return when {
            s.shopName.isBlank() -> {
                _uiState.update { it.copy(error = "يرجى إدخال اسم الصالون") }
                false
            }
            s.wilayaCode == null -> {
                _uiState.update { it.copy(error = "يرجى اختيار الولاية") }
                false
            }
            s.city.isBlank() -> {
                _uiState.update { it.copy(error = "يرجى إدخال المدينة") }
                false
            }
            else -> true
        }
    }

    // ── Create Shop (onboarding) ───────────────────────────────────────────────
    fun createShop(ownerId: String, googleUid: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val s = _uiState.value
            val shop = BarberShop(
                ownerId        = ownerId,
                shopName       = s.shopName,
                city           = s.city,
                wilayaCode     = s.wilayaCode,
                address        = s.address.ifBlank { null },
                whatsappNumber = s.whatsappNumber.ifBlank { null },
                services       = s.services,
                openingFrom    = s.openingFrom,
                openingTo      = s.openingTo,
                priceMin       = s.priceMin,
                priceMax       = s.priceMax,
                googleUid      = googleUid
            )
            barberShopRepository.createShop(shop)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    // ── Update Shop (edit mode) ────────────────────────────────────────────────
    fun updateShop() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val s = _uiState.value
            val shop = BarberShop(
                id             = s.shopId,
                ownerId        = s.ownerId,
                shopName       = s.shopName,
                city           = s.city,
                wilayaCode     = s.wilayaCode,
                address        = s.address.ifBlank { null },
                whatsappNumber = s.whatsappNumber.ifBlank { null },
                services       = s.services,
                openingFrom    = s.openingFrom,
                openingTo      = s.openingTo,
                priceMin       = s.priceMin,
                priceMax       = s.priceMax
            )
            barberShopRepository.updateShop(shop)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun clearError()   = _uiState.update { it.copy(error = null) }
    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}

/** UI state for shop creation / edit. */
data class CreateShopUiState(
    // Shared fields
    val shopId         : String        = "",
    val ownerId        : String        = "",
    val isEditMode     : Boolean       = false,
    // Step 1
    val shopName       : String        = "",
    val wilayaCode     : Int?          = null,
    val city           : String        = "",
    val address        : String        = "",
    val whatsappNumber : String        = "",
    // Step 2
    val services       : MutableList<String> = mutableListOf(),
    val openingFrom    : String        = "09:00",
    val openingTo      : String        = "20:00",
    val priceMin       : Int           = 200,
    val priceMax       : Int           = 2000,
    // Status
    val isLoading      : Boolean       = false,
    val isSuccess      : Boolean       = false,
    val error          : String?       = null
)
