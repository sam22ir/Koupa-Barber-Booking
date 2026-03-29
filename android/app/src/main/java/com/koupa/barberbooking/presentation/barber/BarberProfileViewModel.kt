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
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for BarberProfileScreen.
 * Manages the barber's shop data.
 */
@HiltViewModel
class BarberProfileViewModel @Inject constructor(
private val barberShopRepository: BarberShopRepository,
val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberProfileUiState())
    val uiState: StateFlow<BarberProfileUiState> = _uiState.asStateFlow()

    fun loadShopData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Get owner ID from UserPreferences
            val ownerId = userPreferences.getUserId()
            if (ownerId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "User ID not found"
                )
                return@launch
            }

            // Load shop data by owner ID
            barberShopRepository.getShopByOwnerId(ownerId)
                .onSuccess { shop ->
                    _uiState.value = _uiState.value.copy(
                        shop = shop,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for BarberProfileScreen.
 */
data class BarberProfileUiState(
    // Shop data
    val shop: BarberShop? = null,
    val isLoading: Boolean = false,
    
    // Error state
    val error: String? = null
)