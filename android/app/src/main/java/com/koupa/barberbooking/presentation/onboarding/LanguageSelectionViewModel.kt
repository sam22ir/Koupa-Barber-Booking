package com.koupa.barberbooking.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koupa.barberbooking.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Language Selection Screen
 */
@HiltViewModel
class LanguageSelectionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageSelectionUiState())
    val uiState: StateFlow<LanguageSelectionUiState> = _uiState.asStateFlow()

    fun selectLanguage(language: String) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun saveLanguage(userPreferences: UserPreferences) {
        viewModelScope.launch {
            val language = _uiState.value.selectedLanguage
            userPreferences.saveLanguage(language)
            userPreferences.setOnboardingComplete()
        }
    }
}

/**
 * UI State for Language Selection Screen
 */
data class LanguageSelectionUiState(
    val selectedLanguage: String = "ar"
)