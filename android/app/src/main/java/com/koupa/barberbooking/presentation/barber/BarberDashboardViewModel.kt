package com.koupa.barberbooking.presentation.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.data.repository.BarberShopRepositoryImpl
import com.koupa.barberbooking.domain.model.Appointment
import com.koupa.barberbooking.domain.model.AppointmentStatus
import com.koupa.barberbooking.domain.model.Debt
import com.koupa.barberbooking.domain.model.DebtSummary
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import com.koupa.barberbooking.domain.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * UI state for the barber dashboard.
 */
data class BarberDashboardUiState(
    val todayAppointments: List<TodayAppointment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val shopId: String? = null,
    val debts: List<Debt> = emptyList(),
    val debtSummary: DebtSummary? = null,
    val isLoadingDebts: Boolean = false,
    val showAddDebtDialog: Boolean = false,
    val showEditDebtDialog: Boolean = false,
    val editingDebt: Debt? = null,
    val showDeleteDebtConfirmation: Boolean = false,
    val deletingDebt: Debt? = null
)

/**
 * UI model for displaying today's appointments on the dashboard.
 */
data class TodayAppointment(
    val id: String,
    val clientName: String,
    val service: String,
    val time: String,
    val status: String
)

/**
 * ViewModel for the Barber Dashboard screen.
 * Fetches today's appointments for the barber's shop.
 */
@HiltViewModel
class BarberDashboardViewModel @Inject constructor(
    private val repository: BarberShopRepository,
    private val authRepository: AuthRepository,
    private val debtRepository: DebtRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberDashboardUiState())
    val uiState: StateFlow<BarberDashboardUiState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("ar"))
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ar"))

    /**
     * Load today's appointments for the current barber's shop.
     * Automatically fetches the shop ID from the current user.
     */
    fun loadTodayAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Get current user
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "يجب تسجيل الدخول أولاً") 
                    }
                    return@launch
                }
                
                // Get shop by owner ID
                val shopResult = (repository as? BarberShopRepositoryImpl)?.getShopByOwnerId(user.id)
                if (shopResult == null || shopResult.isFailure) {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "لم يتم العثور على متجر") 
                    }
                    return@launch
                }
                
                val shop = shopResult.getOrNull()
                if (shop == null) {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "لم يتم العثور على متجر") 
                    }
                    return@launch
                }
                
                // Load appointments for today
                loadAppointmentsForShop(shop.id)
                // Load debts for the shop
                loadDebts(shop.id)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message ?: "فشل تحميل البيانات") 
                }
            }
        }
    }

    /**
     * Load today's appointments for a specific shop.
     * @param shopId The ID of the barber's shop
     */
    fun loadAppointmentsForShop(shopId: String) {
        if (shopId.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, shopId = shopId) }
            
            repository.getShopAppointments(shopId, LocalDate.now())
                .onSuccess { appointments ->
                    val todayAppts = appointments.map { it.toTodayAppointment() }
                    _uiState.update { 
                        it.copy(isLoading = false, todayAppointments = todayAppts) 
                    }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = e.message ?: "فشل تحميل المواعيد") 
                    }
                }
        }
    }

    /**
     * Load debts and debt summary for a specific shop.
     * @param shopId The ID of the barber's shop
     */
    fun loadDebts(shopId: String) {
        if (shopId.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDebts = true) }
            
            // Load debts
            debtRepository.getShopDebts(shopId)
                .onSuccess { debts ->
                    // Load debt summary
                    debtRepository.getDebtSummary(shopId)
                        .onSuccess { summary ->
                            _uiState.update { 
                                it.copy(
                                    isLoadingDebts = false,
                                    debts = debts,
                                    debtSummary = summary
                                )
                            }
                        }
                        .onFailure { _ ->
                            _uiState.update { 
                                it.copy(
                                    isLoadingDebts = false,
                                    debts = debts,
                                    debtSummary = null
                                )
                            }
                        }
                }
                .onFailure { _ ->
                    _uiState.update { 
                        it.copy(
                            isLoadingDebts = false,
                            debts = emptyList(),
                            debtSummary = null
                        )
                    }
                }
        }
    }

    /**
     * Create a new debt.
     */
    fun createDebt(customerName: String, amount: Int, notes: String?) {
        val state = _uiState.value
        val shopId = state.shopId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDebts = true) }
            
            debtRepository.createDebt(shopId, customerName, amount, notes)
                .onSuccess { 
                    loadDebts(shopId)
                    hideAddDebtDialog()
                }
                .onFailure { _ ->
                    _uiState.update { 
                        it.copy(isLoadingDebts = false) 
                    }
                }
        }
    }

    /**
     * Update an existing debt.
     */
    fun updateDebt(debtId: String, customerName: String, amount: Int, notes: String?, isPaid: Boolean) {
        val state = _uiState.value
        val shopId = state.shopId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDebts = true) }
            
            debtRepository.updateDebt(debtId, customerName, amount, notes, isPaid)
                .onSuccess { 
                    loadDebts(shopId)
                    hideEditDebtDialog()
                }
                .onFailure { _ ->
                    _uiState.update { 
                        it.copy(isLoadingDebts = false) 
                    }
                }
        }
    }

    /**
     * Delete a debt.
     */
    fun deleteDebt(debtId: String) {
        val state = _uiState.value
        val shopId = state.shopId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDebts = true) }
            
            debtRepository.deleteDebt(debtId)
                .onSuccess { 
                    loadDebts(shopId)
                    hideDeleteDebtConfirmation()
                }
                .onFailure { _ ->
                    _uiState.update { 
                        it.copy(isLoadingDebts = false) 
                    }
                }
        }
    }

    /**
     * Mark a debt as paid.
     */
    fun markDebtAsPaid(debtId: String) {
        val state = _uiState.value
        val shopId = state.shopId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDebts = true) }
            
            debtRepository.markAsPaid(debtId)
                .onSuccess { 
                    loadDebts(shopId)
                }
                .onFailure { _ ->
                    _uiState.update { 
                        it.copy(isLoadingDebts = false) 
                    }
                }
        }
    }

    /**
     * Show add debt dialog.
     */
    fun showAddDebtDialog() {
        _uiState.update { it.copy(showAddDebtDialog = true) }
    }

    /**
     * Hide add debt dialog.
     */
    fun hideAddDebtDialog() {
        _uiState.update { it.copy(showAddDebtDialog = false) }
    }

    /**
     * Show edit debt dialog.
     */
    fun showEditDebtDialog(debt: Debt) {
        _uiState.update { 
            it.copy(
                showEditDebtDialog = true,
                editingDebt = debt
            )
        }
    }

    /**
     * Hide edit debt dialog.
     */
    fun hideEditDebtDialog() {
        _uiState.update { 
            it.copy(
                showEditDebtDialog = false,
                editingDebt = null
            )
        }
    }

    /**
     * Show delete debt confirmation.
     */
    fun showDeleteDebtConfirmation(debt: Debt) {
        _uiState.update { 
            it.copy(
                showDeleteDebtConfirmation = true,
                deletingDebt = debt
            )
        }
    }

    /**
     * Hide delete debt confirmation.
     */
    fun hideDeleteDebtConfirmation() {
        _uiState.update { 
            it.copy(
                showDeleteDebtConfirmation = false,
                deletingDebt = null
            )
        }
    }

    /**
     * Refresh today's appointments.
     */
    fun refresh() {
        loadTodayAppointments()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Convert domain Appointment to UI TodayAppointment.
     */
    private fun Appointment.toTodayAppointment(): TodayAppointment {
        val statusLabel = when (status) {
            AppointmentStatus.PENDING -> "قيد الانتظار"
            AppointmentStatus.CONFIRMED -> "مؤكد"
            AppointmentStatus.CANCELLED -> "ملغي"
            AppointmentStatus.COMPLETED -> "مكتمل"
        }
        
        return TodayAppointment(
            id = id,
            clientName = customerName ?: "عميل",
            service = serviceName ?: "خدمة",
            time = timeSlot.format(timeFormatter),
            status = statusLabel
        )
    }
}