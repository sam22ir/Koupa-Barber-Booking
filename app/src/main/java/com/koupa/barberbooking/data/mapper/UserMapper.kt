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

    fun toInsertMap(phoneNumber: String, role: String, language: String = "ar"): Map<String, Any?> {
        return mapOf(
            "phone_number" to phoneNumber,
            "role" to role,
            "language" to language
        )
    }

    fun toUpdateMap(fullName: String?, role: String?, language: String?): Map<String, Any?> {
        return buildMap {
            fullName?.let { put("full_name", it) }
            role?.let { put("role", it) }
            language?.let { put("language", it) }
        }
    }
}
