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

data class DaySchedule(
    val dayName: String,      // Arabic name
    val dayCode: String,      // "sat", "sun", etc.
    val isOpen: Boolean,
    val openingFrom: String,
    val openingTo: String
)

data class WorkingHoursUiState(
    val days: List<DaySchedule> = listOf(
        DaySchedule("السبت", "sat", true, "09:00", "20:00"),
        DaySchedule("الأحد", "sun", true, "09:00", "20:00"),
        DaySchedule("الاثنين", "mon", true, "09:00", "20:00"),
        DaySchedule("الثلاثاء", "tue", true, "09:00", "20:00"),
        DaySchedule("الأربعاء", "wed", true, "09:00", "20:00"),
        DaySchedule("الخميس", "thu", true, "09:00", "20:00"),
        DaySchedule("الجمعة", "fri", false, "09:00", "20:00")
    ),
    val shop: BarberShop? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class WorkingHoursViewModel @Inject constructor(
    private val repository: BarberShopRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkingHoursUiState())
    val uiState: StateFlow<WorkingHoursUiState> = _uiState.asStateFlow()

    init {
        loadWorkingHours()
    }

    private fun loadWorkingHours() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val ownerId = userPreferences.getUserId() ?: return@launch
            repository.getShopByOwnerId(ownerId)
                .onSuccess { shop ->
                    if (shop != null) {
                        val dayCodes = listOf("sat","sun","mon","tue","wed","thu","fri")
                        val dayNames = listOf("السبت","الأحد","الاثنين","الثلاثاء","الأربعاء","الخميس","الجمعة")
                        val days = dayCodes.mapIndexed { i, code ->
                            DaySchedule(
                                dayName = dayNames[i],
                                dayCode = code,
                                isOpen = shop.workingDays.contains(code),
                                openingFrom = shop.openingFrom,
                                openingTo = shop.openingTo
                            )
                        }
                        _uiState.update { it.copy(days = days, shop = shop, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }

    fun toggleDay(dayCode: String) {
        _uiState.update { state ->
            state.copy(days = state.days.map {
                if (it.dayCode == dayCode) it.copy(isOpen = !it.isOpen) else it
            })
        }
    }

    fun updateOpeningTime(dayCode: String, from: String, to: String) {
        _uiState.update { state ->
            state.copy(days = state.days.map {
                if (it.dayCode == dayCode) it.copy(openingFrom = from, openingTo = to) else it
            })
        }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val shop = _uiState.value.shop ?: return@launch
            val openDays = _uiState.value.days.filter { it.isOpen }.map { it.dayCode }
            val firstOpenDay = _uiState.value.days.firstOrNull { it.isOpen }

            val updatedShop = shop.copy(
                workingDays = openDays,
                openingFrom = firstOpenDay?.openingFrom ?: "09:00",
                openingTo = firstOpenDay?.openingTo ?: "20:00"
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
fun clearSuccessMessage() { _uiState.update { it.copy(successMessage = null, isSaved = false) } }
fun clearError() { _uiState.update { it.copy(error = null) } }
}