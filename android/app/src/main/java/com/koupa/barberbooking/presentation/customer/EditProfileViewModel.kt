package com.koupa.barberbooking.presentation.customer

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
import javax.inject.Inject

data class EditProfileUiState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val whatsappNumber: String = "",
    val profilePhotoUrl: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val userId = userPreferences.getUserId()
                if (userId.isNullOrEmpty()) {
                    // Use local data as fallback
                    _uiState.update {
                        it.copy(
                            fullName = userPreferences.getUserName() ?: "",
                            phoneNumber = userPreferences.getPhoneNumber() ?: "",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                val result = supabase.from("users")
                    .select(columns = Columns.list("full_name", "phone_number", "bio", "profile_photo_url", "whatsapp_number")) {
                        filter { eq("id", userId) }
                    }
                    .decodeList<JsonObject>()
                    .firstOrNull()

                if (result != null) {
                    _uiState.update {
                        it.copy(
                            fullName = result["full_name"]?.jsonPrimitive?.content ?: "",
                            phoneNumber = result["phone_number"]?.jsonPrimitive?.content ?: "",
                            bio = result["bio"]?.jsonPrimitive?.content ?: "",
                            profilePhotoUrl = result["profile_photo_url"]?.jsonPrimitive?.content ?: "",
                            whatsappNumber = result["whatsapp_number"]?.jsonPrimitive?.content ?: "",
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onFullNameChange(value: String) { _uiState.update { it.copy(fullName = value) } }
    fun onBioChange(value: String) { _uiState.update { it.copy(bio = value) } }
    fun onWhatsappChange(value: String) { _uiState.update { it.copy(whatsappNumber = value) } }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val userId = userPreferences.getUserId()
                if (!userId.isNullOrEmpty()) {
                    supabase.from("users").update(
                        {
                            set("full_name", _uiState.value.fullName)
                            set("bio", _uiState.value.bio)
                            set("whatsapp_number", _uiState.value.whatsappNumber)
                        }
                    ) {
                        filter { eq("id", userId) }
                    }
                }
                // Save locally too
                userPreferences.saveUserName(_uiState.value.fullName)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSaving = false) }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
    fun clearSaved() { _uiState.update { it.copy(isSaved = false) } }
}