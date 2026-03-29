package com.koupa.barberbooking.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user preferences stored in SharedPreferences.
 * Stores phone number, user name, email, language preference, and first launch status.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    private val _language = MutableStateFlow(getLanguage())
    val language: StateFlow<String> = _language.asStateFlow()

    /**
     * Get saved phone number.
     * @return Phone number or null if not set
     */
    fun getPhoneNumber(): String? {
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }

    /**
     * Save phone number to preferences.
     * @param phoneNumber The phone number to save
     */
    fun savePhoneNumber(phoneNumber: String) {
        prefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    /**
     * Get user's display name.
     * @return User name or null if not set
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Save user's display name.
     * @param name The name to save
     */
    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    /**
     * Get saved email address.
     * @return Email or null if not set
     */
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    /**
     * Save email address to preferences.
     * @param email The email to save
     */
    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    /**
     * Get saved user ID.
     * @return User ID or null if not set
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /**
     * Save user ID to preferences.
     * @param userId The user ID to save
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Get saved language preference.
     * @return "ar" (Arabic) or "en" (English). Defaults to "ar"
     */
    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    /**
     * Save language preference.
     * @param language "ar" or "en"
     */
    fun saveLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
        _language.value = language
    }

    /**
     * Check if user has completed onboarding.
     */
    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    /**
     * Mark onboarding as complete.
     */
    fun setOnboardingComplete() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }

    /**
     * Check if this is the first app launch.
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        if (isFirst) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        }
        return isFirst
    }

    /**
     * Clear all user data (for logout).
     */
    fun clearUserData() {
        prefs.edit()
            .remove(KEY_PHONE_NUMBER)
            .remove(KEY_USER_NAME)
            .remove(KEY_EMAIL)
            .putBoolean(KEY_ONBOARDING_COMPLETE, false)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "koupa_user_prefs"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val DEFAULT_LANGUAGE = "ar"
    }
}