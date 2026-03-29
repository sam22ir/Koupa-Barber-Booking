package com.koupa.barberbooking.domain.usecase

import com.koupa.barberbooking.domain.repository.AuthState
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.utils.PhoneNumberValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, otpCode: String): Flow<AuthState> = flow {
        // For OTP verification, we just need to validate and sign in with the phone number
        // The OTP is not used in Supabase implementation, but we validate the phone number format
        val normalized = PhoneNumberValidator.normalizeToE164(phoneNumber)
        if (normalized == null) {
            emit(AuthState.Error(
                IllegalArgumentException("Invalid phone number format"),
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

        // Delegate to repository for Supabase authentication
        authRepository.signInWithPhone(normalized).collect { state ->
            emit(state)
        }
    }
}
