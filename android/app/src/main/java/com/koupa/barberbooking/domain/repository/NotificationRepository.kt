package com.koupa.barberbooking.domain.repository

/**
 * Repository interface for FCM notification operations.
 */
interface NotificationRepository {
    /**
     * Update FCM token for the current user in Supabase.
     * Called on every app launch when token refreshes.
     */
    suspend fun updateFcmToken(userId: String, token: String): Result<Unit>

    /**
     * Subscribe to a notification topic (e.g., shop-specific updates).
     */
    suspend fun subscribeToTopic(topic: String): Result<Unit>

    /**
     * Unsubscribe from a notification topic.
     */
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit>
}
