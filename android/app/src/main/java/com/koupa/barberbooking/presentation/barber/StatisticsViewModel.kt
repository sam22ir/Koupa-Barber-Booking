package com.koupa.barberbooking.presentation.barber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import java.time.LocalDate
import javax.inject.Inject

data class StatisticsUiState(
    val totalBookings: Int = 0,
    val revenue: Int = 0,
    val newCustomers: Int = 0,
    val avgRating: Double = 0.0,
    val reviewCount: Int = 0,
    val isLoading: Boolean = false,
    val selectedPeriod: String = "weekly", // "weekly", "monthly", "yearly"
    val error: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val userId = userPreferences.getUserId() ?: return@launch

                // Get shop ID
                val shopResult = supabase.from("barbershops")
                    .select(columns = Columns.list("id", "average_rating", "review_count")) {
                        filter { eq("owner_id", userId) }
                    }
                    .decodeList<JsonObject>()
                    .firstOrNull()

                val shopId = shopResult?.get("id")?.jsonPrimitive?.content ?: return@launch
                val avgRating = shopResult["average_rating"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val reviewCount = shopResult["review_count"]?.jsonPrimitive?.intOrNull ?: 0

                // Calculate date range based on period
                val now = LocalDate.now()
                val startDate = when (_uiState.value.selectedPeriod) {
                    "weekly" -> now.minusWeeks(1)
                    "monthly" -> now.minusMonths(1)
                    "yearly" -> now.minusYears(1)
                    else -> now.minusWeeks(1)
                }

                // Count total bookings
                val bookingsCount = supabase.from("appointments")
                    .select(columns = Columns.list("id")) {
                        filter {
                            eq("shop_id", shopId)
                            gte("appointment_date", startDate.toString())
                        }
                    }
                    .decodeList<JsonObject>()
                    .size

                // Count completed bookings for revenue estimate
                val completedBookings = supabase.from("appointments")
                    .select(columns = Columns.list("id")) {
                        filter {
                            eq("shop_id", shopId)
                            eq("status", "completed")
                            gte("appointment_date", startDate.toString())
                        }
                    }
                    .decodeList<JsonObject>()
                    .size

                // Estimate revenue (avg 800 DZD per booking)
                val estimatedRevenue = completedBookings * 800

                // Count unique customers
                val allAppointments = supabase.from("appointments")
                    .select(columns = Columns.list("customer_id")) {
                        filter {
                            eq("shop_id", shopId)
                            gte("appointment_date", startDate.toString())
                        }
                    }
                    .decodeList<JsonObject>()

                val uniqueCustomers = allAppointments
                    .mapNotNull { it["customer_id"]?.jsonPrimitive?.content }
                    .toSet()
                    .size

                _uiState.update {
                    it.copy(
                        totalBookings = bookingsCount,
                        revenue = estimatedRevenue,
                        newCustomers = uniqueCustomers,
                        avgRating = avgRating,
                        reviewCount = reviewCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onPeriodChanged(period: String) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadStatistics()
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}