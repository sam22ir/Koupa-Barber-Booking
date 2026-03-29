package com.koupa.barberbooking.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.domain.model.Notification
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    /**
     * Load notifications for the current user.
     * Automatically fetches the current user from AuthRepository.
     */
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val user = authRepository.getCurrentUser()
                if (user == null) {
                    _uiState.update { 
                        it.copy(isLoading = false, error = "يجب تسجيل الدخول أولاً") 
                    }
                    return@launch
                }
                
                _uiState.update { it.copy(currentUserId = user.id) }
                
                notificationRepository.getNotifications(user.id)
                    .onSuccess { notifications ->
                        _uiState.update { it.copy(isLoading = false, notifications = notifications) }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = e.message ?: "فشل تحميل الإشعارات") 
                }
            }
        }
    }

    fun markAllAsRead() {
        val userId = _uiState.value.currentUserId ?: return
        viewModelScope.launch {
            notificationRepository.markAllAsRead(userId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            notifications = state.notifications.map { it.copy(isRead = true) }
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            notifications = state.notifications.map {
                                if (it.id == notificationId) it.copy(isRead = true) else it
                            }
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Get notifications from today.
     */
    fun getTodayNotifications(): List<Notification> {
        val today = LocalDate.now()
        return _uiState.value.notifications.filter { notif ->
            val notifDate = notif.createdAt.toLocalDate()
            notifDate.isEqual(today)
        }
    }

    /**
     * Get notifications from yesterday.
     */
    fun getYesterdayNotifications(): List<Notification> {
        val today = LocalDate.now()
        val yesterday = today.minus(1, ChronoUnit.DAYS)
        return _uiState.value.notifications.filter { notif ->
            val notifDate = notif.createdAt.toLocalDate()
            notifDate.isEqual(yesterday)
        }
    }

    /**
     * Get notifications older than yesterday.
     */
    fun getOlderNotifications(): List<Notification> {
        val today = LocalDate.now()
        val yesterday = today.minus(1, ChronoUnit.DAYS)
        return _uiState.value.notifications.filter { notif ->
            val notifDate = notif.createdAt.toLocalDate()
            notifDate.isBefore(yesterday)
        }
    }
}
