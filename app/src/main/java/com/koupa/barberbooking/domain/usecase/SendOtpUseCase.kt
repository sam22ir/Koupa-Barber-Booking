package com.koupa.barberbooking.domain.usecase

import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.AuthState
import com.koupa.barberbooking.utils.PhoneNumberValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for sending OTP to a phone number.
 * Validates the phone number before sending.
 */
class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): Flow<AuthState> = flow {
        emit(AuthState.Loading)

        // Normalize phone number to E.164 format
        val normalized = PhoneNumberValidator.normalizeToE164(phoneNumber)
        if (normalized == null) {
            emit(AuthState.Error(
                IllegalArgumentException("Invalid phone number"),
                "رقم الهاتف غير صالح"
            ))
            return@flow
        }

        // Validate it's an Algerian mobile number
        if (!PhoneNumberValidator.isValidAlgeriaMobile(normalized)) {
            emit(AuthState.Error(
                IllegalArgumentException("Not an Algerian mobile number"),
                "يجب أن يكون رقم جزائري محمول"
            ))
            return@flow
        }

        // Delegate to repository
        authRepository.sendOtp(normalized).collect { state ->
            emit(state)
        }
    }
}
