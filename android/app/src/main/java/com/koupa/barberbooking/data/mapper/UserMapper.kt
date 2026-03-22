package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.User
import com.koupa.barberbooking.domain.model.UserRole
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant

/**
 * Mapper for converting between Supabase JSON and domain User model.
 */
object UserMapper {
    fun fromJson(json: JsonObject): User {
        return User(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            phoneNumber = json["phone_number"]?.jsonPrimitive?.content ?: "",
            fullName = json["full_name"]?.jsonPrimitive?.content,
            role = UserRole.fromString(json["role"]?.jsonPrimitive?.content ?: "customer"),
            fcmToken = json["fcm_token"]?.jsonPrimitive?.content,
            language = json["language"]?.jsonPrimitive?.content ?: "ar",
            createdAt = try {
                Instant.parse(json["created_at"]?.jsonPrimitive?.content ?: "")
            } catch (e: Exception) {
                Instant.now()
            }
        )
    }

    fun createInsertDto(phoneNumber: String, role: String, language: String = "ar") =
        UserInsertDto(phoneNumber, role, language)

    fun createUpdateDto(fullName: String?, role: String?, language: String?) =
        UserUpdateDto(fullName, role, language)
}

@kotlinx.serialization.Serializable
data class UserInsertDto(
    val phone_number: String,
    val role: String,
    val language: String
)

@kotlinx.serialization.Serializable
data class UserUpdateDto(
    val full_name: String? = null,
    val role: String? = null,
    val language: String? = null
)
