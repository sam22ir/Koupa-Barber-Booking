package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.BarberShop
import kotlinx.serialization.json.*

/**
 * Mapper for converting between Supabase JSON and domain BarberShop model.
 */
object BarberShopMapper {

    fun fromJson(json: JsonObject): BarberShop {
        return BarberShop(
            id              = json["id"]?.jsonPrimitive?.content ?: "",
            ownerId         = json["owner_id"]?.jsonPrimitive?.content ?: "",
            shopName        = json["shop_name"]?.jsonPrimitive?.content ?: "",
            city            = json["city"]?.jsonPrimitive?.content ?: "",
            wilayaCode      = json["wilaya_code"]?.jsonPrimitive?.content?.toIntOrNull(),
            address         = json["address"]?.jsonPrimitive?.contentOrNull,
            latitude        = json["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            longitude       = json["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            isActive        = json["is_active"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true,
            bio             = json["bio"]?.jsonPrimitive?.contentOrNull,
            services        = json["services"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            openingFrom     = json["opening_from"]?.jsonPrimitive?.content?.take(5) ?: "09:00",
            openingTo       = json["opening_to"]?.jsonPrimitive?.content?.take(5) ?: "20:00",
            workingDays     = json["working_days"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            priceMin        = json["price_min"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
            priceMax        = json["price_max"]?.jsonPrimitive?.content?.toIntOrNull() ?: 5000,
            whatsappNumber  = json["whatsapp_number"]?.jsonPrimitive?.contentOrNull,
            googleUid       = json["google_uid"]?.jsonPrimitive?.contentOrNull,
            profilePhotoUrl = json["profile_photo_url"]?.jsonPrimitive?.contentOrNull,
            distanceKm      = json["distance_meters"]?.jsonPrimitive?.content?.toDoubleOrNull()?.div(1000.0)
        )
    }

    /** Build a JSON body for INSERT into barbershops (create flow). */
    fun toInsertJson(shop: BarberShop): JsonObject = buildJsonObject {
        put("owner_id",        shop.ownerId)
        put("shop_name",       shop.shopName)
        put("city",            shop.city)
        shop.wilayaCode?.let { put("wilaya_code", it) }
        shop.address?.let    { put("address", it) }
        shop.whatsappNumber?.let { put("whatsapp_number", it) }
        shop.bio?.let        { put("bio", it) }
        shop.googleUid?.let  { put("google_uid", it) }
        put("services",     JsonArray(shop.services.map { JsonPrimitive(it) }))
        put("working_days", JsonArray(shop.workingDays.map { JsonPrimitive(it) }))
        put("opening_from", shop.openingFrom + ":00")   // Supabase time needs seconds
        put("opening_to",   shop.openingTo  + ":00")
        put("price_min",    shop.priceMin)
        put("price_max",    shop.priceMax)
        put("is_active",    true)
    }

    /** Build a JSON body for UPDATE (edit shop flow). */
    fun toUpdateJson(shop: BarberShop): JsonObject = buildJsonObject {
        put("shop_name",     shop.shopName)
        put("city",          shop.city)
        shop.wilayaCode?.let { put("wilaya_code", it) }
        shop.address?.let    { put("address", it) }
        shop.bio?.let        { put("bio", it) }
        shop.whatsappNumber?.let { put("whatsapp_number", it) }
        put("services",     JsonArray(shop.services.map { JsonPrimitive(it) }))
        put("working_days", JsonArray(shop.workingDays.map { JsonPrimitive(it) }))
        put("opening_from", shop.openingFrom + ":00")
        put("opening_to",   shop.openingTo  + ":00")
        put("price_min",    shop.priceMin)
        put("price_max",    shop.priceMax)
    }
}
