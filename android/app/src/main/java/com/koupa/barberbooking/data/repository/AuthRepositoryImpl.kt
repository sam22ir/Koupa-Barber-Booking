package com.koupa.barberbooking.data.repository

import com.koupa.barberbooking.data.mapper.UserMapper
import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.AuthState
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Uses direct Supabase database lookup for phone-based authentication.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val supabase = SupabaseClientFactory.client

    override suspend fun signInWithPhone(phoneNumber: String): Flow<AuthState> = flow {
        emit(AuthState.Loading)

        try {
            // Query Supabase users table by phone_number
            val existingUser = fetchUserByPhone(phoneNumber)

            if (existingUser != null) {
                emit(AuthState.Authenticated(existingUser))
            } else {
                // Create new user with minimal data
                val newUser = createUser(phoneNumber)
                emit(AuthState.Authenticated(newUser))
            }
        } catch (e: Exception) {
            emit(AuthState.Error(e, "فشل تسجيل الدخول: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): User? {
        // No session-based auth without Firebase - ViewModel tracks current user in state
        return null
    }

    override suspend fun updateUserRole(phoneNumber: String, role: UserRole, fullName: String): Result<User> {
        return try {
            val updates = UserMapper.createUpdateDto(fullName, role.name.lowercase(), null)

            supabase.from("users")
                .update(updates) {
                    filter {
                        eq("phone_number", phoneNumber)
                    }
                }

            // Fetch updated user
            val user = fetchUserByPhone(phoneNumber)
                ?: return Result.failure(Exception("Failed to fetch updated user"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        // Sign out from Supabase only (no Firebase)
        supabase.auth.signOut()
    }

    private suspend fun fetchUserByPhone(phone: String): User? {
        return try {
            val result = supabase.from("users")
                .select {
                    filter {
                        eq("phone_number", phone)
                    }
                }
                .decodeList<JsonObject>()
                .firstOrNull()

            result?.let { jsonObject -> UserMapper.fromJson(jsonObject) }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun createUser(phoneNumber: String, role: String = "customer"): User {
        val insertData = UserMapper.createInsertDto(phoneNumber, role, "ar")

        supabase.from("users")
            .insert(insertData)

        return fetchUserByPhone(phoneNumber)
            ?: throw Exception("Failed to create user")
    }
}
