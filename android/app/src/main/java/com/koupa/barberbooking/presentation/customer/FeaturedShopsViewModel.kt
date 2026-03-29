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

data class FeaturedShopsUiState(
    val shops: List<BarberShop> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortBy: String = "rating" // "nearest", "rating", "newest"
)

@HiltViewModel
class FeaturedShopsViewModel @Inject constructor(
    private val repository: BarberShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeaturedShopsUiState())
    val uiState: StateFlow<FeaturedShopsUiState> = _uiState.asStateFlow()

    init {
        loadShops()
    }

    fun loadShops() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getNearbyShops(latitude = 36.7538, longitude = 3.0588, radiusKm = 100.0)
                .onSuccess { shops ->
                    val sorted = sortShops(shops, _uiState.value.sortBy)
                    _uiState.update { it.copy(shops = sorted, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun onSortChanged(sortBy: String) {
        _uiState.update { it.copy(sortBy = sortBy, shops = sortShops(it.shops, sortBy)) }
    }

    private fun sortShops(shops: List<BarberShop>, sortBy: String): List<BarberShop> {
        return when (sortBy) {
            "rating" -> shops.sortedByDescending { it.averageRating ?: 0.0 }
            "newest" -> shops.reversed()
            else -> shops.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}