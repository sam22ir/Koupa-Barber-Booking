package com.koupa.barberbooking.domain.repository

import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 * Uses direct Supabase database lookup for phone-based authentication.
 */
interface AuthRepository {
    /**
     * Sign in with phone number via direct Supabase lookup.
     * @param phoneNumber E.164 formatted phone number (+213XXXXXXXXX)
     * @return Flow emitting AuthState updates
     */
    suspend fun signInWithPhone(phoneNumber: String): Flow<AuthState>

    /**
     * Get current authenticated user (returns null - ViewModel tracks current user in state).
     */
    suspend fun getCurrentUser(): User?

    /**
     * Update user role after initial auth.
     * @param phoneNumber User's phone number for lookup
     * @param role Selected role (CUSTOMER or BARBER)
     * @param fullName User's full name
     */
    suspend fun updateUserRole(phoneNumber: String, role: UserRole, fullName: String): Result<User>

    /**
     * Sign out the current user from Supabase.
     */
    suspend fun signOut()
}

/**
 * Sealed class representing authentication states.
 */
sealed class AuthState {
    data class Authenticated(val user: User) : AuthState()
    data class Error(val exception: Throwable, val message: String) : AuthState()
    object Loading : AuthState()
}
