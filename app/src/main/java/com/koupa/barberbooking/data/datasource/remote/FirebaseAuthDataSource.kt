package com.koupa.barberbooking.data.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Authentication operations.
 * Handles phone number verification and OTP validation.
 */
@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    /**
     * Send OTP to the given phone number.
     * @param phoneNumber E.164 formatted phone number (+213XXXXXXXXX)
     * @param callbacks Callback for verification state changes
     */
    fun sendOtp(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Verify OTP code and sign in.
     * @param verificationId Firebase verification ID
     * @param otpCode 6-digit OTP code
     * @return Firebase user on success
     */
    suspend fun verifyOtp(verificationId: String, otpCode: String): Result<FirebaseUser> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let { user ->
                Result.success(user)
            } ?: Result.failure(Exception("Authentication failed: no user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current Firebase ID token for Supabase integration.
     */
    suspend fun getIdToken(): Result<String> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("No authenticated user"))
            val tokenResult = user.getIdToken(true).await()
            tokenResult.token?.let { token ->
                Result.success(token)
            } ?: Result.failure(Exception("Failed to get ID token"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current Firebase user.
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Sign out from Firebase.
     */
    fun signOut() {
        auth.signOut()
    }
}
