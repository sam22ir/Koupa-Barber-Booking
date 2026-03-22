package com.koupa.barberbooking.data.repository

import com.koupa.barberbooking.data.datasource.remote.FirebaseMessagingDataSource
import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import com.koupa.barberbooking.domain.repository.NotificationRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationRepository.
 * Bridges FCM token management with Supabase user records.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val fcmDataSource: FirebaseMessagingDataSource
) : NotificationRepository {

    private val supabase = SupabaseClientFactory.client

    override suspend fun updateFcmToken(userId: String, token: String): Result<Unit> {
        return try {
            supabase.from("users")
                .update(buildJsonObject { put("fcm_token", token) }) {
                    filter { eq("id", userId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun subscribeToTopic(topic: String): Result<Unit> =
        fcmDataSource.subscribeToTopic(topic)

    override suspend fun unsubscribeFromTopic(topic: String): Result<Unit> =
        fcmDataSource.unsubscribeFromTopic(topic)
}
