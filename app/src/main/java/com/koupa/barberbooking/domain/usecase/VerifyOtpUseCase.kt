package com.koupa.barberbooking.domain.usecase

import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for verifying OTP code.
 * Validates OTP format before verification.
 */
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(verificationId: String, otpCode: String): Flow<AuthState> = flow {
        emit(AuthState.Loading)

        // Validate OTP is 6 digits
        if (otpCode.length != 6 || !otpCode.all { it.isDigit() }) {
            emit(AuthState.Error(
                IllegalArgumentException("Invalid OTP format"),
                "رمز التحقق يجب أن يكون 6 أرقام"
            ))
            return@flow
        }

        // Delegate to repository
        authRepository.verifyOtp(verificationId, otpCode).collect { state ->
            emit(state)
        }
    }
}
