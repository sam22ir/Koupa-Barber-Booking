package com.koupa.barberbooking.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdating: Boolean = false,
    val isUpdated: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // In a real implementation, this would fetch the actual user profile
                // For now, we'll simulate with a placeholder
                _uiState.update { it.copy(isLoading = false, user = createPlaceholderUser()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

private fun createPlaceholderUser(): User {
 // Create a placeholder user for demonstration
 return User(
 id = "user_123",
 phoneNumber = "+213123456789",
 fullName = "مستخدم تجريبي",
 role = com.koupa.barberbooking.domain.model.UserRole.CUSTOMER,
 fcmToken = "token_123",
 language = "ar",
 createdAt = java.time.Instant.now()
 )
 }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }
            try {
                val result = updateProfileUseCase(fullName, email)
                if (result.isSuccess) {
                    _uiState.update { it.copy(isUpdating = false, isUpdated = true) }
                } else {
                    _uiState.update { it.copy(isUpdating = false, error = "فشل تحديث الملف الشخصي") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _uiState.update { it.copy(user = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}