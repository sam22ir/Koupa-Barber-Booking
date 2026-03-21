package com.koupa.barberbooking.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import com.koupa.barberbooking.MainActivity
import com.koupa.barberbooking.R
import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service for Koupa.
 * Handles push notifications for bookings, cancellations, and reminders.
 */
@AndroidEntryPoint
class KoupaFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "KoupaFCM"
        private const val CHANNEL_BOOKINGS = "new_bookings"
        private const val CHANNEL_CANCELLATIONS = "cancellations"
        private const val CHANNEL_REMINDERS = "reminders"
    }

    @Inject
    lateinit var auth: FirebaseAuth

    private val supabase = SupabaseClientFactory.client
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Called when FCM token is refreshed.
     * Updates token in Supabase for the current user.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed: $token")

        serviceScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    // Update FCM token in Supabase
                    val phoneNumber = auth.currentUser?.phoneNumber ?: return@launch
                    supabase.from("users")
                        .update(buildJsonObject { put("fcm_token", token) }) {
                            filter { eq("phone_number", phoneNumber) }
                        }
                    Log.d(TAG, "FCM token updated in Supabase")
                } else {
                    Log.w(TAG, "No authenticated user to update FCM token")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update FCM token", e)
            }
        }
    }

    /**
     * Called when a message is received.
     * Displays notification based on type (booking, cancellation, reminder).
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            val type = remoteMessage.data["type"] ?: "general"
            val channelId = when (type) {
                "new_booking" -> CHANNEL_BOOKINGS
                "cancellation" -> CHANNEL_CANCELLATIONS
                "reminder" -> CHANNEL_REMINDERS
                else -> CHANNEL_BOOKINGS
            }

            showNotification(
                title = notification.title ?: getString(R.string.app_name),
                body = notification.body ?: "",
                channelId = channelId,
                data = remoteMessage.data
            )
        }

        // Handle data-only messages
        if (remoteMessage.data.isNotEmpty() && remoteMessage.notification == null) {
            val type = remoteMessage.data["type"] ?: "general"
            val title = remoteMessage.data["title"] ?: getString(R.string.app_name)
            val body = remoteMessage.data["body"] ?: ""

            val channelId = when (type) {
                "new_booking" -> CHANNEL_BOOKINGS
                "cancellation" -> CHANNEL_CANCELLATIONS
                "reminder" -> CHANNEL_REMINDERS
                else -> CHANNEL_BOOKINGS
            }

            showNotification(title = title, body = body, channelId = channelId, data = remoteMessage.data)
        }
    }

    /**
     * Display a notification with the given parameters.
     */
    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        data: Map<String, String>
    ) {
        createNotificationChannel(channelId)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", data["type"])
            putExtra("notification_data", data.toString())
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(
                if (channelId == CHANNEL_REMINDERS) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * Create notification channel for Android O+.
     */
    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = when (channelId) {
                CHANNEL_BOOKINGS -> getString(R.string.channel_bookings)
                CHANNEL_CANCELLATIONS -> getString(R.string.channel_cancellations)
                CHANNEL_REMINDERS -> getString(R.string.channel_reminders)
                else -> getString(R.string.channel_general)
            }

            val importance = when (channelId) {
                CHANNEL_REMINDERS -> NotificationManager.IMPORTANCE_HIGH
                else -> NotificationManager.IMPORTANCE_DEFAULT
            }

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = getString(R.string.channel_description)
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
