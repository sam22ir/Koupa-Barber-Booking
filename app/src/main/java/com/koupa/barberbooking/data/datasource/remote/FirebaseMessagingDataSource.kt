package com.koupa.barberbooking.data.datasource.remote

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Cloud Messaging operations.
 * Handles FCM token management.
 */
@Singleton
class FirebaseMessagingDataSource @Inject constructor(
    private val messaging: FirebaseMessaging
) {
    /**
     * Get current FCM token.
     * Should be called on app launch and stored in Supabase.
     */
    suspend fun getToken(): Result<String> {
        return try {
            val token = messaging.token.await()
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Subscribe to a topic for targeted notifications.
     */
    suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            messaging.subscribeToTopic(topic).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Unsubscribe from a topic.
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            messaging.unsubscribeFromTopic(topic).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
