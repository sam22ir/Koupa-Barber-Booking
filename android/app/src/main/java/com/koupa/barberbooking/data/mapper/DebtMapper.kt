package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.Debt
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Mapper for converting Debt between domain and database models.
 */
object DebtMapper {

    fun fromJson(json: JsonObject): Debt {
        return Debt(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
            customerName = json["customer_name"]?.jsonPrimitive?.content ?: "",
            amount = json["amount"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
            notes = json["notes"]?.jsonPrimitive?.content,
            isPaid = json["is_paid"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
            createdAt = json["created_at"]?.jsonPrimitive?.content?.let {
                try {
                    java.time.Instant.parse(it).toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            } ?: System.currentTimeMillis(),
            updatedAt = json["updated_at"]?.jsonPrimitive?.content?.let {
                try {
                    java.time.Instant.parse(it).toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            } ?: System.currentTimeMillis()
        )
    }

    fun toInsertJson(shopId: String, customerName: String, amount: Int, notes: String?): JsonObject {
        return buildJsonObject {
            put("shop_id", JsonPrimitive(shopId))
            put("customer_name", JsonPrimitive(customerName))
            put("amount", JsonPrimitive(amount))
            notes?.let { put("notes", JsonPrimitive(it)) }
        }
    }

    fun toUpdateJson(customerName: String, amount: Int, notes: String?, isPaid: Boolean): JsonObject {
        return buildJsonObject {
            put("customer_name", JsonPrimitive(customerName))
            put("amount", JsonPrimitive(amount))
            notes?.let { put("notes", JsonPrimitive(it)) }
            put("is_paid", JsonPrimitive(isPaid))
        }
    }
}
