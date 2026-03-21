package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.BarberShop
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Mapper for converting between Supabase JSON and domain BarberShop model.
 */
object BarberShopMapper {
    fun fromJson(json: JsonObject): BarberShop {
        return BarberShop(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            ownerId = json["owner_id"]?.jsonPrimitive?.content ?: "",
            shopName = json["shop_name"]?.jsonPrimitive?.content ?: "",
            city = json["city"]?.jsonPrimitive?.content ?: "",
            wilayaCode = json["wilaya_code"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
            latitude = json["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            longitude = json["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            isActive = json["is_active"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true,
            distanceKm = json["distance_meters"]?.jsonPrimitive?.content?.toDoubleOrNull()?.div(1000.0)
        )
    }

    fun toInsertMap(
        ownerId: String,
        shopName: String,
        city: String,
        wilayaCode: Int,
        latitude: Double?,
        longitude: Double?
    ): Map<String, Any?> {
        return buildMap {
            put("owner_id", ownerId)
            put("shop_name", shopName)
            put("city", city)
            put("wilaya_code", wilayaCode)
            latitude?.let { put("latitude", it) }
            longitude?.let { put("longitude", it) }
        }
    }
}
