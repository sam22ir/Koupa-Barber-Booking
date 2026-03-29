package com.koupa.barberbooking.presentation.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SlotManagementUiState(
    val isLoading: Boolean = true,
    val shopId: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val weekDates: List<LocalDate> = emptyList(),
    val slotsByDate: Map<LocalDate, List<AvailabilitySlot>> = emptyMap(),
    val error: String? = null
)

/**
 * ViewModel for SlotManagementScreen.
 * Fetches real availability slots from Supabase and handles toggling.
 */
@HiltViewModel
class SlotManagementViewModel @Inject constructor(
    private val repository: BarberShopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SlotManagementUiState())
    val state: StateFlow<SlotManagementUiState> = _state.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * Initialize with the barber's shop ID and load current week slots.
     */
    fun loadSlots(ownerId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Get shop by owner ID
            val shopResult = repository.getShopByOwnerId(ownerId)
            if (shopResult.isFailure) {
                _state.update {
                    it.copy(isLoading = false, error = "فشل تحميل بيانات المتجر")
                }
                return@launch
            }

            val shop = shopResult.getOrNull()
            if (shop == null) {
                _state.update {
                    it.copy(isLoading = false, error = "لم يتم العثور على متجر")
                }
                return@launch
            }

            // Generate current week dates (Sunday to Saturday)
            val today = LocalDate.now()
            val dayOfWeek = today.dayOfWeek.value % 7 // 0=Sunday, 6=Saturday
            val startOfWeek = today.minusDays(dayOfWeek.toLong())
            val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

            _state.update {
                it.copy(
                    shopId = shop.id,
                    weekDates = weekDates,
                    selectedDate = weekDates.first { date ->
                        !date.isBefore(today)
                    }
                )
            }

            // Load slots for all week dates
            loadSlotsForWeek(shop.id, weekDates)
        }
    }

    /**
     * Load availability slots for all dates in the week.
     */
    private suspend fun loadSlotsForWeek(shopId: String, dates: List<LocalDate>) {
        val slotsByDate = mutableMapOf<LocalDate, List<AvailabilitySlot>>()

        for (date in dates) {
            val result = repository.getAvailableSlots(shopId, date)
            result.onSuccess { slots ->
                slotsByDate[date] = slots
            }.onFailure {
                slotsByDate[date] = emptyList()
            }
        }

        _state.update {
            it.copy(isLoading = false, slotsByDate = slotsByDate)
        }
    }

    /**
     * Select a different date.
     */
    fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    /**
     * Toggle a slot's availability (open/closed).
     */
    fun toggleSlot(slot: AvailabilitySlot) {
        viewModelScope.launch {
            val newIsOpen = !slot.isOpen
            val result = repository.toggleSlot(slot.id, newIsOpen)

            result.onSuccess { updatedSlot ->
                _state.update { currentState ->
                    val currentSlots = currentState.slotsByDate[slot.slotDate] ?: emptyList()
                    val updatedSlots = currentSlots.map {
                        if (it.id == slot.slotDate.toString()) updatedSlot else it
                    }
                    currentState.copy(
                        slotsByDate = currentState.slotsByDate.toMutableMap().apply {
                            this[slot.slotDate] = updatedSlots
                        }
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(error = e.message ?: "فشل تحديث الموعد")
                }
            }
        }
    }

    /**
     * Select all slots for the current day.
     */
    fun selectAllSlots() {
        viewModelScope.launch {
            val currentState = _state.value
            val date = currentState.selectedDate
            val currentSlots = currentState.slotsByDate[date] ?: emptyList()

            // Toggle all closed slots to open
            for (slot in currentSlots.filter { !it.isOpen }) {
                repository.toggleSlot(slot.id, true)
            }

            // Reload slots for this date
            val result = repository.getAvailableSlots(currentState.shopId, date)
            result.onSuccess { slots ->
                _state.update {
                    it.copy(
                        slotsByDate = it.slotsByDate.toMutableMap().apply {
                            this[date] = slots
                        }
                    )
                }
            }
        }
    }

    /**
     * Deselect all slots for the current day.
     */
    fun deselectAllSlots() {
        viewModelScope.launch {
            val currentState = _state.value
            val date = currentState.selectedDate
            val currentSlots = currentState.slotsByDate[date] ?: emptyList()

            // Toggle all open (non-booked) slots to closed
            for (slot in currentSlots.filter { it.isOpen && !it.isBooked }) {
                repository.toggleSlot(slot.id, false)
            }

            // Reload slots for this date
            val result = repository.getAvailableSlots(currentState.shopId, date)
            result.onSuccess { slots ->
                _state.update {
                    it.copy(
                        slotsByDate = it.slotsByDate.toMutableMap().apply {
                            this[date] = slots
                        }
                    )
                }
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
