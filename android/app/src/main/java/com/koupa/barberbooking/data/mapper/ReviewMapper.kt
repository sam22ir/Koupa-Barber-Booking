package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.Review
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Mapper for converting Review between domain and database models.
 */
object ReviewMapper {

    fun fromJson(json: JsonObject): Review {
        return Review(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
            customerId = json["customer_id"]?.jsonPrimitive?.content ?: "",
            customerName = json["customer_name"]?.jsonPrimitive?.content,
            rating = json["rating"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
            comment = json["comment"]?.jsonPrimitive?.content,
            createdAt = json["created_at"]?.jsonPrimitive?.content?.let {
                try {
                    // Parse ISO timestamp and convert to millis
                    java.time.Instant.parse(it).toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            } ?: System.currentTimeMillis()
        )
    }

    fun toInsertJson(review: Review): JsonObject {
        return buildJsonObject {
            put("shop_id", JsonPrimitive(review.shopId))
            put("customer_id", JsonPrimitive(review.customerId))
            put("rating", JsonPrimitive(review.rating))
            review.comment?.let { put("comment", JsonPrimitive(it)) }
        }
    }

    fun toUpdateJson(rating: Int, comment: String?): JsonObject {
        return buildJsonObject {
            put("rating", JsonPrimitive(rating))
            comment?.let { put("comment", JsonPrimitive(it)) }
        }
    }
}