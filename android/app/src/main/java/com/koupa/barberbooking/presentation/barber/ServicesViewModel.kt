package com.koupa.barberbooking.presentation.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceItem(
    val name: String,
    val priceMin: Int,
    val priceMax: Int
)

data class ServicesUiState(
    val services: List<ServiceItem> = emptyList(),
    val shop: BarberShop? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val showAddDialog: Boolean = false,
    val editingIndex: Int? = null,
    val error: String? = null
)

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val repository: BarberShopRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val ownerId = userPreferences.getUserId() ?: return@launch
            repository.getShopByOwnerId(ownerId)
                .onSuccess { shop ->
                    if (shop != null) {
                        val serviceItems = shop.services.map { name ->
                            ServiceItem(name, shop.priceMin, shop.priceMax)
                        }
                        _uiState.update { it.copy(services = serviceItems, shop = shop, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true, editingIndex = null) } }
    fun showEditDialog(index: Int) { _uiState.update { it.copy(showAddDialog = true, editingIndex = index) } }
    fun hideDialog() { _uiState.update { it.copy(showAddDialog = false, editingIndex = null) } }

    fun addService(name: String, priceMin: Int, priceMax: Int) {
        _uiState.update { state ->
            val newService = ServiceItem(name, priceMin, priceMax)
            val services = if (state.editingIndex != null) {
                state.services.toMutableList().apply { set(state.editingIndex, newService) }
            } else {
                state.services + newService
            }
            state.copy(services = services, showAddDialog = false, editingIndex = null)
        }
    }

    fun removeService(index: Int) {
        _uiState.update { state ->
            state.copy(services = state.services.toMutableList().apply { removeAt(index) })
        }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val shop = _uiState.value.shop ?: return@launch
            val serviceNames = _uiState.value.services.map { it.name }
            val minPrice = _uiState.value.services.minOfOrNull { it.priceMin } ?: 0
            val maxPrice = _uiState.value.services.maxOfOrNull { it.priceMax } ?: 5000

            val updatedShop = shop.copy(
                services = serviceNames,
                priceMin = minPrice,
                priceMax = maxPrice
            )

            repository.updateShop(updatedShop)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isSaving = false) }
                }
        }
    }

    fun clearSaved() { _uiState.update { it.copy(isSaved = false) } }
    fun clearError() { _uiState.update { it.copy(error = null) } }
}