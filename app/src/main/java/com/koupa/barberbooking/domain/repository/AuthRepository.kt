package com.koupa.barberbooking.domain.repository

import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * Handles Firebase Phone Auth and Supabase user management.
 */
interface AuthRepository {
    /**
     * Send OTP to the given phone number.
     * @param phoneNumber E.164 formatted phone number (+213XXXXXXXXX)
     * @return Flow emitting AuthState updates
     */
    suspend fun sendOtp(phoneNumber: String): Flow<AuthState>

    /**
     * Verify OTP code and complete authentication.
     * @param verificationId Firebase verification ID from sendOtp
     * @param otpCode 6-digit OTP code entered by user
     * @return Flow emitting AuthState updates
     */
    suspend fun verifyOtp(verificationId: String, otpCode: String): Flow<AuthState>

    /**
     * Get current authenticated user from Supabase.
     */
    suspend fun getCurrentUser(): User?

    /**
     * Update user role after initial auth.
     * @param role Selected role (CUSTOMER or BARBER)
     * @param fullName User's full name
     */
    suspend fun updateUserRole(role: UserRole, fullName: String): Result<User>

    /**
     * Sign out the current user.
     */
    suspend fun signOut()
}

/**
 * Sealed class representing authentication states.
 */
sealed class AuthState {
    data class OtpSent(val verificationId: String) : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val exception: Throwable, val message: String) : AuthState()
    object Loading : AuthState()
}
