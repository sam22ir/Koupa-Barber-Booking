package com.koupa.barberbooking.presentation.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.model.Review
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import com.koupa.barberbooking.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for BarberShopProfileScreen.
 * Manages shop data, reviews, and available slots.
 */
@HiltViewModel
class BarberShopProfileViewModel @Inject constructor(
    private val barberShopRepository: BarberShopRepository,
    private val reviewRepository: ReviewRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberShopProfileUiState())
    val uiState: StateFlow<BarberShopProfileUiState> = _uiState.asStateFlow()

    fun loadShopData(shopId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load shop details
            barberShopRepository.getShopById(shopId)
                .onSuccess { shop ->
                    _uiState.value = _uiState.value.copy(
                        shop = shop,
                        isLoading = false
                    )
                    // Load slots for today after shop is loaded
                    loadAvailableSlots(shopId, LocalDate.now())
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }

            // Load reviews
            loadReviews(shopId)
        }
    }

    fun loadAvailableSlots(shopId: String, date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSlots = true)

            barberShopRepository.getAvailableSlots(shopId, date)
                .onSuccess { slots ->
                    val morningSlots = slots.filter { it.slotTime.hour < 12 }
                    val afternoonSlots = slots.filter { it.slotTime.hour >= 12 }

                    _uiState.value = _uiState.value.copy(
                        morningSlots = morningSlots,
                        afternoonSlots = afternoonSlots,
                        selectedDate = date,
                        isLoadingSlots = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingSlots = false,
                        error = error.message
                    )
                }
        }
    }

    fun selectDate(date: LocalDate) {
        val shopId = _uiState.value.shop?.id ?: return
        loadAvailableSlots(shopId, date)
    }

    fun selectSlot(slot: AvailabilitySlot) {
        _uiState.value = _uiState.value.copy(selectedSlot = slot)
    }

    fun loadReviews(shopId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingReviews = true)

            // Load reviews
            reviewRepository.getShopReviews(shopId)
                .onSuccess { reviews ->
                    _uiState.value = _uiState.value.copy(
                        reviews = reviews,
                        isLoadingReviews = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingReviews = false,
                        error = error.message
                    )
                }

            // Load rating summary
            reviewRepository.getShopRatingSummary(shopId)
                .onSuccess { (avgRating, count) ->
                    _uiState.value = _uiState.value.copy(
                        averageRating = avgRating,
                        reviewCount = count
                    )
                }

            // Check if user has already reviewed
            val userId = userPreferences.getUserId()
            if (userId != null) {
                reviewRepository.hasUserReviewed(shopId, userId)
                    .onSuccess { hasReviewed ->
                        _uiState.value = _uiState.value.copy(hasUserReviewed = hasReviewed)
                    }
            }
        }
    }

    fun submitReview(shopId: String, rating: Int, comment: String?) {
        viewModelScope.launch {
            val userId = userPreferences.getUserId() ?: return@launch

            _uiState.value = _uiState.value.copy(isSubmittingReview = true)

            val result = if (_uiState.value.hasUserReviewed) {
                // Update existing review
                val existingReview = _uiState.value.reviews.find { it.customerId == userId }
                if (existingReview != null) {
                    reviewRepository.updateReview(existingReview.id, rating, comment)
                } else {
                    Result.failure(Exception("Review not found"))
                }
            } else {
                // Create new review
                reviewRepository.createReview(shopId, userId, rating, comment)
            }

            result
                .onSuccess { _ ->
                    // Refresh reviews
                    loadReviews(shopId)
                    _uiState.value = _uiState.value.copy(
                        isSubmittingReview = false,
                        showReviewDialog = false,
                        reviewSubmitted = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmittingReview = false,
                        error = error.message
                    )
                }
        }
    }

    fun deleteReview(reviewId: String, shopId: String) {
        viewModelScope.launch {
            val userId = userPreferences.getUserId() ?: return@launch

            reviewRepository.deleteReview(reviewId, userId)
                .onSuccess {
                    // Refresh reviews
                    loadReviews(shopId)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    fun showReviewDialog() {
        _uiState.value = _uiState.value.copy(showReviewDialog = true)
    }

    fun hideReviewDialog() {
        _uiState.value = _uiState.value.copy(showReviewDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetReviewSubmitted() {
        _uiState.value = _uiState.value.copy(reviewSubmitted = false)
    }
}

/**
 * UI state for BarberShopProfileScreen.
 */
data class BarberShopProfileUiState(
    // Shop data
    val shop: BarberShop? = null,
    val isLoading: Boolean = false,

    // Slots data
    val morningSlots: List<AvailabilitySlot> = emptyList(),
    val afternoonSlots: List<AvailabilitySlot> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedSlot: AvailabilitySlot? = null,
    val isLoadingSlots: Boolean = false,

    // Reviews data
    val reviews: List<Review> = emptyList(),
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0,
    val isLoadingReviews: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val hasUserReviewed: Boolean = false,
    val showReviewDialog: Boolean = false,
    val reviewSubmitted: Boolean = false,

    // Error state
    val error: String? = null
)
