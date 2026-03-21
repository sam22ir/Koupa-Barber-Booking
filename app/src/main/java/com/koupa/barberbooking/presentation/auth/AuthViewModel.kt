package com.koupa.barberbooking.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.AuthState
import com.koupa.barberbooking.domain.usecase.SendOtpUseCase
import com.koupa.barberbooking.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication screens (Phone Entry, OTP, Role Selection).
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var verificationId: String? = null

    fun onPhoneNumberChanged(number: String) {
        _uiState.update { it.copy(phoneNumber = number, error = null) }
    }

    fun sendOtp() {
        viewModelScope.launch {
            sendOtpUseCase(_uiState.value.phoneNumber).collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is AuthState.OtpSent -> {
                        verificationId = state.verificationId
                        _uiState.update {
                            it.copy(isLoading = false, showOtpField = true, error = null)
                        }
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

    fun onOtpChanged(otp: String) {
        if (otp.length <= 6 && otp.all { it.isDigit() }) {
            _uiState.update { it.copy(otpCode = otp, error = null) }
        }
    }

    fun verifyOtp() {
        val id = verificationId ?: return
        viewModelScope.launch {
            verifyOtpUseCase(id, _uiState.value.otpCode).collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is AuthState.Authenticated -> {
                        handleAuthenticatedUser(state.user)
                    }
                    is AuthState.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = state.message)
                        }
                    }
                    else -> {}
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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.updateUserRole(role, name)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, isAuthenticated = true, user = user)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "فشل التسجيل")
                    }
                }
        }
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
    val phoneNumber: String = "",
    val otpCode: String = "",
    val fullName: String = "",
    val selectedRole: UserRole? = null,
    val isLoading: Boolean = false,
    val showOtpField: Boolean = false,
    val showRoleSelection: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
