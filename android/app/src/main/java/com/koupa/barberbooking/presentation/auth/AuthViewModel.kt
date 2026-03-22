package com.koupa.barberbooking.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.AuthState
import com.koupa.barberbooking.domain.usecase.PhoneAuthUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication screens (Phone Entry, Role Selection).
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val phoneAuthUseCase: PhoneAuthUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChanged(number: String) {
        _uiState.update { it.copy(phoneNumber = number, error = null) }
    }

    fun signInWithPhone() {
        viewModelScope.launch {
            phoneAuthUseCase(_uiState.value.phoneNumber).collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is AuthState.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = state.message)
                        }
                    }
                    is AuthState.Authenticated -> {
                        handleAuthenticatedUser(state.user)
                    }
                }
            }
        }
    }

    fun onRoleSelected(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, error = null) }
    }

    fun completeRegistration() {
        val role = _uiState.value.selectedRole ?: return
        val name = _uiState.value.fullName
        val phoneNumber = _uiState.value.user?.phoneNumber ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.updateUserRole(phoneNumber, role, name)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = role == UserRole.CUSTOMER,
                            // Barbers go to Google Sign-In → create shop flow
                            shouldNavigateToBarberOnboarding = role == UserRole.BARBER,
                            user = user
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "فشل التسجيل")
                    }
                }
        }
    }

    fun onBarberOnboardingNavigated() {
        _uiState.update { it.copy(shouldNavigateToBarberOnboarding = false) }
    }

    private fun handleAuthenticatedUser(user: User) {
        if (user.fullName == null) {
            // New user - needs role selection
            _uiState.update {
                it.copy(isLoading = false, showRoleSelection = true, user = user)
            }
        } else {
            // Existing user - go to home
            _uiState.update {
                it.copy(isLoading = false, isAuthenticated = true, user = user)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for authentication screens.
 */
data class AuthUiState(
    val phoneNumber      : String    = "",
    val fullName         : String    = "",
    val selectedRole     : UserRole? = null,
    val isLoading        : Boolean   = false,
    val showRoleSelection: Boolean   = false,
    val isAuthenticated  : Boolean   = false,
    val shouldNavigateToBarberOnboarding: Boolean = false,
    val user             : User?     = null,
    val error            : String?   = null
)
