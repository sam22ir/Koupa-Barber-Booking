package com.koupa.barberbooking.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.usecase.CreateAppointmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingUiState(
    val isLoading: Boolean = false,
    val isConfirmed: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the booking flow (date → time → confirmation).
 * Wires BookConfirmationScreen's confirm button to Supabase createAppointment.
 */
@HiltViewModel
class BookingViewModel @Inject constructor(
    private val createAppointmentUseCase: CreateAppointmentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BookingUiState())
    val state: StateFlow<BookingUiState> = _state.asStateFlow()

    /**
     * Confirm the appointment booking via Supabase Edge Function.
     * @param customerId current user ID
     * @param shopId the chosen barbershop
     * @param slotId the chosen availability slot
     */
    fun confirmBooking(customerId: String, shopId: String, slotId: String) {
        if (customerId.isBlank() || shopId.isBlank() || slotId.isBlank()) {
            _state.update { it.copy(error = "بيانات الحجز ناقصة") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            createAppointmentUseCase(customerId, shopId, slotId)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isConfirmed = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "فشل إنشاء الحجز") }
                }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
