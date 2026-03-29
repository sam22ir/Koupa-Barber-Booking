package com.koupa.barberbooking.presentation.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.domain.model.Debt
import com.koupa.barberbooking.domain.model.DebtSummary
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import com.koupa.barberbooking.domain.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for DebtManagementScreen.
 * Manages debt records for barbers.
 */
@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    private val barberShopRepository: BarberShopRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DebtUiState())
    val uiState: StateFlow<DebtUiState> = _uiState.asStateFlow()

    init {
        loadShopAndDebts()
    }

    private fun loadShopAndDebts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Get current user's shop
            val userId = userPreferences.getUserId()
            if (userId != null) {
                barberShopRepository.getShopByOwnerId(userId)
                    .onSuccess { shop ->
                        if (shop != null) {
                            _uiState.value = _uiState.value.copy(shopId = shop.id)
                            loadDebts(shop.id)
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "No shop found for this user"
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "User not logged in"
                )
            }
        }
    }

    fun loadDebts(shopId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load debts
            debtRepository.getShopDebts(shopId)
                .onSuccess { debts ->
                    _uiState.value = _uiState.value.copy(
                        debts = debts,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }

            // Load summary
            debtRepository.getDebtSummary(shopId)
                .onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(debtSummary = summary)
                }
        }
    }

    fun createDebt(customerName: String, amount: Int, notes: String?) {
        val shopId = _uiState.value.shopId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)

            debtRepository.createDebt(shopId, customerName, amount, notes)
                .onSuccess { _ ->
                    // Refresh debts
                    loadDebts(shopId)
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        showAddDialog = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = error.message
                    )
                }
        }
    }

    fun updateDebt(debtId: String, customerName: String, amount: Int, notes: String?, isPaid: Boolean) {
        val shopId = _uiState.value.shopId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)

            debtRepository.updateDebt(debtId, customerName, amount, notes, isPaid)
                .onSuccess { _ ->
                    // Refresh debts
                    loadDebts(shopId)
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        showEditDialog = false,
                        editingDebt = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = error.message
                    )
                }
        }
    }

    fun deleteDebt(debtId: String) {
        val shopId = _uiState.value.shopId ?: return

        viewModelScope.launch {
            debtRepository.deleteDebt(debtId)
                .onSuccess {
                    // Refresh debts
                    loadDebts(shopId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun markAsPaid(debtId: String) {
        val shopId = _uiState.value.shopId ?: return

        viewModelScope.launch {
            debtRepository.markAsPaid(debtId)
                .onSuccess {
                    // Refresh debts
                    loadDebts(shopId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun showEditDialog(debt: Debt) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            editingDebt = debt
        )
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            editingDebt = null
        )
    }

    fun showDeleteConfirmation(debt: Debt) {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmation = true,
            deletingDebt = debt
        )
    }

    fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmation = false,
            deletingDebt = null
        )
    }

    fun filterByStatus(showPaid: Boolean) {
        _uiState.value = _uiState.value.copy(showPaidDebts = showPaid)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for DebtManagementScreen.
 */
data class DebtUiState(
    val shopId: String? = null,
    val debts: List<Debt> = emptyList(),
    val debtSummary: DebtSummary? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingDebt: Debt? = null,
    val showDeleteConfirmation: Boolean = false,
    val deletingDebt: Debt? = null,
    val showPaidDebts: Boolean = false,
    val error: String? = null
)
