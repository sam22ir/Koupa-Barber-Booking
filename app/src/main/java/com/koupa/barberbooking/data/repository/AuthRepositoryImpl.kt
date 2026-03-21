package com.koupa.barberbooking.data.repository

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.koupa.barberbooking.data.datasource.remote.FirebaseAuthDataSource
import com.koupa.barberbooking.data.mapper.UserMapper
import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.AuthState
import com.koupa.barberbooking.utils.FirebaseErrorMapper
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Implementation of AuthRepository.
 * Integrates Firebase Phone Auth with Supabase user management.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    private val supabase = SupabaseClientFactory.client
    private var storedVerificationId: String? = null
    private var storedResendToken: PhoneAuthProvider.ForceResendingToken? = null

    override suspend fun sendOtp(phoneNumber: String): Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-retrieval or instant verification
                // We'll handle this in verifyOtp
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                trySend(AuthState.Error(e, FirebaseErrorMapper.mapAuthException(e)))
                close()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                storedResendToken = token
                trySend(AuthState.OtpSent(verificationId))
            }
        }

        firebaseAuthDataSource.sendOtp(phoneNumber, callbacks)

        awaitClose { /* Cleanup if needed */ }
    }

    override suspend fun verifyOtp(verificationId: String, otpCode: String): Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)

        firebaseAuthDataSource.verifyOtp(verificationId, otpCode)
            .onSuccess { firebaseUser ->
                // Get Firebase ID token
                firebaseAuthDataSource.getIdToken()
                    .onSuccess { idToken ->
                        // Exchange Firebase token with Supabase
                        try {
                            supabase.auth.signInWith(IDToken) {
                                this.idToken = idToken
                            }

                            // Fetch or create user in Supabase
                            val phone = firebaseUser.phoneNumber ?: ""
                            val existingUser = fetchUserByPhone(phone)

                            if (existingUser != null) {
                                trySend(AuthState.Authenticated(existingUser))
                            } else {
                                // Create new user
                                val newUser = createUser(phone)
                                trySend(AuthState.Authenticated(newUser))
                            }
                        } catch (e: Exception) {
                            trySend(AuthState.Error(e, "Failed to sync with Supabase: ${e.message}"))
                        }
                    }
                    .onFailure { e ->
                        trySend(AuthState.Error(e, FirebaseErrorMapper.mapOtpException(e as? Exception ?: Exception(e.message))))
                    }
            }
            .onFailure { e ->
                trySend(AuthState.Error(e, FirebaseErrorMapper.mapOtpException(e as? Exception ?: Exception(e.message))))
            }

        awaitClose { /* Cleanup */ }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuthDataSource.getCurrentUser() ?: return null
        return fetchUserByPhone(firebaseUser.phoneNumber ?: "")
    }

    override suspend fun updateUserRole(role: UserRole, fullName: String): Result<User> {
        return try {
            val firebaseUser = firebaseAuthDataSource.getCurrentUser()
                ?: return Result.failure(IllegalStateException("Not authenticated"))

            val updates = UserMapper.toUpdateMap(fullName, role.name.lowercase(), null)

            supabase.from("users")
                .update(updates) {
                    filter {
                        eq("phone_number", firebaseUser.phoneNumber ?: "")
                    }
                }

            // Fetch updated user
            val user = fetchUserByPhone(firebaseUser.phoneNumber ?: "")
                ?: return Result.failure(Exception("Failed to fetch updated user"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuthDataSource.signOut()
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

    private suspend fun createUser(phone: String): User {
        val insertData = UserMapper.toInsertMap(phone, "customer", "ar")

        supabase.from("users")
            .insert(insertData)

        return fetchUserByPhone(phone)
            ?: throw Exception("Failed to create user")
    }
}
