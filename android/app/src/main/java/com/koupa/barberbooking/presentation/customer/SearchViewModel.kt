package com.koupa.barberbooking.presentation.customer

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

data class SearchUiState(
    val searchQuery: String = "",
    val shops: List<BarberShop> = emptyList(),
    val filteredShops: List<BarberShop> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String = "all" // "all", "city", "rating", "services"
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BarberShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadAllShops()
    }

    private fun loadAllShops() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Load shops with large radius to get all
            repository.getNearbyShops(latitude = 36.7538, longitude = 3.0588, radiusKm = 100.0)
                .onSuccess { shops ->
                    _uiState.update { it.copy(shops = shops, filteredShops = shops, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterShops(query, _uiState.value.selectedFilter)
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        filterShops(_uiState.value.searchQuery, filter)
    }

    private fun filterShops(query: String, filter: String) {
        val allShops = _uiState.value.shops
        val filtered = if (query.isBlank()) {
            allShops
        } else {
            allShops.filter { shop ->
                when (filter) {
                    "city" -> shop.city.contains(query, ignoreCase = true)
                    "rating" -> (shop.averageRating ?: 0.0) >= query.toDoubleOrNull() ?: 0.0
                    "services" -> shop.services.any { it.contains(query, ignoreCase = true) }
                    else -> shop.shopName.contains(query, ignoreCase = true) ||
                            shop.city.contains(query, ignoreCase = true) ||
                            shop.services.any { it.contains(query, ignoreCase = true) }
                }
            }
        }
        _uiState.update { it.copy(filteredShops = filtered) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}