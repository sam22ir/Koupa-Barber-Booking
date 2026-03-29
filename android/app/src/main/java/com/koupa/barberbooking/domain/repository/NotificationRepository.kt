package com.koupa.barberbooking.domain.repository

import com.koupa.barberbooking.domain.model.Notification

/**
 * Repository interface for notification operations.
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

    /**
     * Get all notifications for a user.
     */
    suspend fun getNotifications(userId: String): Result<List<Notification>>

    /**
     * Mark all notifications as read for a user.
     */
    suspend fun markAllAsRead(userId: String): Result<Unit>

    /**
     * Mark a specific notification as read.
     */
    suspend fun markAsRead(notificationId: String): Result<Unit>
}
