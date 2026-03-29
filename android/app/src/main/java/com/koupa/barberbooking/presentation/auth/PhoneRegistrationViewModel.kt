package com.koupa.barberbooking.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.usecase.VerifyOtpUseCase
import com.koupa.barberbooking.domain.usecase.PhoneAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhoneRegistrationUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val verificationId: String = "",
    val isPhoneValid: Boolean = false,
    val isOtpSent: Boolean = false,
    val isVerifying: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PhoneRegistrationViewModel @Inject constructor(
    private val phoneAuthUseCase: PhoneAuthUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneRegistrationUiState())
    val uiState: StateFlow<PhoneRegistrationUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
        validatePhoneNumber(phoneNumber)
    }

    fun onOtpCodeChange(code: String) {
        _uiState.update { it.copy(otpCode = code) }
    }

    private fun validatePhoneNumber(phoneNumber: String) {
        // Simple validation - check if it's a valid Algerian number format
        val isValid = phoneNumber.length >= 9 && phoneNumber.all { it.isDigit() }
        _uiState.update { it.copy(isPhoneValid = isValid) }
    }

    fun sendOtpCode() {
        val phoneNumber = _uiState.value.phoneNumber
        if (phoneNumber.isBlank() || !validatePhoneNumberFormat(phoneNumber)) {
            _uiState.update { it.copy(error = "رقم الهاتف غير صالح") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, error = null) }
            try {
                // Use the PhoneAuthUseCase to send the OTP
                phoneAuthUseCase(phoneNumber)
                _uiState.update { it.copy(isVerifying = false, isOtpSent = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isVerifying = false, error = e.message) }
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, error = null) }
            try {
                val result = verifyOtpUseCase(_uiState.value.phoneNumber, _uiState.value.otpCode)
                result.collect { state ->
                    when (state) {
                        is com.koupa.barberbooking.domain.repository.AuthState.Authenticated -> {
                            _uiState.update { it.copy(isVerifying = false, isVerified = true) }
                        }
                        is com.koupa.barberbooking.domain.repository.AuthState.Error -> {
                            _uiState.update { it.copy(isVerifying = false, error = state.message ?: "رمز التحقق غير صحيح") }
                        }
                        is com.koupa.barberbooking.domain.repository.AuthState.Loading -> {
                            // Already showing loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isVerifying = false, error = e.message) }
            }
        }
    }

    private fun validatePhoneNumberFormat(phoneNumber: String): Boolean {
        // Basic validation for Algerian phone numbers
        return phoneNumber.startsWith("0") && phoneNumber.length == 10 ||
                phoneNumber.startsWith("+213") && phoneNumber.length == 12 ||
                phoneNumber.startsWith("213") && phoneNumber.length == 11
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}