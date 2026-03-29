package com.koupa.barberbooking.data.repository

import com.koupa.barberbooking.data.datasource.remote.SupabaseClientFactory
import com.koupa.barberbooking.data.mapper.BarberShopMapper
import com.koupa.barberbooking.domain.model.Appointment
import com.koupa.barberbooking.domain.model.AppointmentStatus
import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BarberShopRepository.
 * Uses Supabase Postgrest and Functions for data operations.
 */
@Singleton
class BarberShopRepositoryImpl @Inject constructor() : BarberShopRepository {

    private val supabase = SupabaseClientFactory.client

    override suspend fun getNearbyShops(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        wilayaCode: Int?
    ): Result<List<BarberShop>> {
        return try {
            val response = supabase.functions.invoke(
                function = "get-nearby-shops",
                body = buildJsonObject {
                    put("p_lat", latitude)
                    put("p_lon", longitude)
                    put("p_radius_km", radiusKm)
                    wilayaCode?.let { put("p_wilaya_code", it) }
                }
            )

            val shops = Json.decodeFromString<JsonArray>(response.bodyAsText()).map { json ->
                BarberShopMapper.fromJson(json.jsonObject)
            }

            Result.success(shops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShopById(shopId: String): Result<BarberShop> {
        return try {
            val result = supabase.from("barbershops")
                .select {
                    filter {
                        eq("id", shopId)
                        eq("is_active", true)
                    }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Shop not found"))

            Result.success(BarberShopMapper.fromJson(result))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createShop(shop: BarberShop): Result<BarberShop> {
        return try {
            val insertData = BarberShopMapper.toInsertJson(shop)
            supabase.from("barbershops").insert(insertData)

            // Fetch the newly created shop
            val created = supabase.from("barbershops")
                .select { filter { eq("owner_id", shop.ownerId) } }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Failed to fetch created shop"))

            Result.success(BarberShopMapper.fromJson(created))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShop(shop: BarberShop): Result<BarberShop> {
        return try {
            val updates = BarberShopMapper.toUpdateJson(shop)
            supabase.from("barbershops")
                .update(updates) { filter { eq("id", shop.id) } }
            Result.success(shop)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

override suspend fun getShopByOwnerId(ownerId: String): Result<BarberShop?> {
        return try {
            val result = supabase.from("barbershops")
                .select { filter { eq("owner_id", ownerId) } }
                .decodeList<JsonObject>()
.firstOrNull()
            Result.success(result?.let { BarberShopMapper.fromJson(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableSlots(
        shopId: String,
        date: LocalDate
    ): Result<List<AvailabilitySlot>> {
        return try {
            val result = supabase.from("availability_slots")
                .select {
                    filter {
                        eq("shop_id", shopId)
                        eq("slot_date", date.toString())
                        eq("is_open", true)
                        eq("is_booked", false)
                    }
                }
                .decodeList<JsonObject>()

            val slots = result.map { json ->
                AvailabilitySlot(
                    id = json["id"]?.jsonPrimitive?.content ?: "",
                    shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
                    slotDate = LocalDate.parse(json["slot_date"]?.jsonPrimitive?.content ?: ""),
                    slotTime = LocalTime.parse(json["slot_time"]?.jsonPrimitive?.content ?: ""),
                    isOpen = json["is_open"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true,
                    isBooked = json["is_booked"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
                )
            }

            Result.success(slots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSlots(
        shopId: String,
        date: LocalDate,
        times: List<String>
    ): Result<List<AvailabilitySlot>> {
        return try {
            val inserts = times.map { time ->
                buildJsonObject {
                    put("shop_id", shopId)
                    put("slot_date", date.toString())
                    put("slot_time", time)
                    put("is_open", true)
                    put("is_booked", false)
                }
            }

            supabase.from("availability_slots")
                .insert(inserts)

            // Return created slots
            getAvailableSlots(shopId, date)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleSlot(slotId: String, isOpen: Boolean): Result<AvailabilitySlot> {
        return try {
            supabase.from("availability_slots")
                .update(buildJsonObject { put("is_open", isOpen) }) {
                    filter { eq("id", slotId) }
                }

            // Fetch updated slot
            val result = supabase.from("availability_slots")
                .select {
                    filter { eq("id", slotId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Slot not found"))

            val slot = AvailabilitySlot(
                id = result["id"]?.jsonPrimitive?.content ?: "",
                shopId = result["shop_id"]?.jsonPrimitive?.content ?: "",
                slotDate = LocalDate.parse(result["slot_date"]?.jsonPrimitive?.content ?: ""),
                slotTime = LocalTime.parse(result["slot_time"]?.jsonPrimitive?.content ?: ""),
                isOpen = result["is_open"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true,
                isBooked = result["is_booked"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
            )

            Result.success(slot)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAppointment(
        customerId: String,
        shopId: String,
        slotId: String
    ): Result<Appointment> {
        return try {
            val response = supabase.functions.invoke(
                function = "create-appointment",
                body = buildJsonObject {
                    put("p_customer_id", customerId)
                    put("p_shop_id", shopId)
                    put("p_slot_id", slotId)
                }
            )

            val appointmentId = try {
                Json.decodeFromString<JsonPrimitive>(response.bodyAsText()).content
            } catch (e: Exception) {
                return Result.failure(Exception("Invalid response from create-appointment"))
            }

            // Fetch created appointment
            val result = supabase.from("appointments")
                .select {
                    filter { eq("id", appointmentId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Failed to fetch created appointment"))

            val appointment = Appointment(
                id = result["id"]?.jsonPrimitive?.content ?: "",
                customerId = result["customer_id"]?.jsonPrimitive?.content ?: "",
                shopId = result["shop_id"]?.jsonPrimitive?.content ?: "",
                slotId = result["slot_id"]?.jsonPrimitive?.content ?: "",
                appointmentDate = LocalDate.parse(result["appointment_date"]?.jsonPrimitive?.content ?: ""),
                timeSlot = LocalTime.parse(result["time_slot"]?.jsonPrimitive?.content ?: ""),
                status = AppointmentStatus.fromString(result["status"]?.jsonPrimitive?.content ?: "pending"),
                paymentMethod = result["payment_method"]?.jsonPrimitive?.content ?: "cash_on_arrival"
            )

            Result.success(appointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(appointmentId: String, userId: String): Result<Boolean> {
        return try {
            supabase.functions.invoke(
                function = "cancel-appointment",
                body = buildJsonObject {
                    put("p_appointment_id", appointmentId)
                    put("p_user_id", userId)
                }
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAppointmentStatus(
        appointmentId: String,
        status: AppointmentStatus
    ): Result<Appointment> {
        return try {
            supabase.from("appointments")
                .update(buildJsonObject { put("status", status.toApiString()) }) {
                    filter { eq("id", appointmentId) }
                }

            // Fetch updated appointment
            val result = supabase.from("appointments")
                .select {
                    filter { eq("id", appointmentId) }
                }
                .decodeList<JsonObject>()
                .firstOrNull()
                ?: return Result.failure(Exception("Appointment not found"))

            val appointment = Appointment(
                id = result["id"]?.jsonPrimitive?.content ?: "",
                customerId = result["customer_id"]?.jsonPrimitive?.content ?: "",
                shopId = result["shop_id"]?.jsonPrimitive?.content ?: "",
                slotId = result["slot_id"]?.jsonPrimitive?.content ?: "",
                appointmentDate = LocalDate.parse(result["appointment_date"]?.jsonPrimitive?.content ?: ""),
                timeSlot = LocalTime.parse(result["time_slot"]?.jsonPrimitive?.content ?: ""),
                status = AppointmentStatus.fromString(result["status"]?.jsonPrimitive?.content ?: "pending"),
                paymentMethod = result["payment_method"]?.jsonPrimitive?.content ?: "cash_on_arrival"
            )

            Result.success(appointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCustomerAppointments(customerId: String): Result<List<Appointment>> {
        return try {
            val result = supabase.from("appointments")
                .select {
                    filter { eq("customer_id", customerId) }
                }
                .decodeList<JsonObject>()

            val appointments = result.map { json ->
                Appointment(
                    id = json["id"]?.jsonPrimitive?.content ?: "",
                    customerId = json["customer_id"]?.jsonPrimitive?.content ?: "",
                    shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
                    slotId = json["slot_id"]?.jsonPrimitive?.content ?: "",
                    appointmentDate = LocalDate.parse(json["appointment_date"]?.jsonPrimitive?.content ?: ""),
                    timeSlot = LocalTime.parse(json["time_slot"]?.jsonPrimitive?.content ?: ""),
                    status = AppointmentStatus.fromString(json["status"]?.jsonPrimitive?.content ?: "pending"),
                    paymentMethod = json["payment_method"]?.jsonPrimitive?.content ?: "cash_on_arrival"
                )
            }

            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getShopAppointments(
        shopId: String,
        date: LocalDate
    ): Result<List<Appointment>> {
        return try {
            val result = supabase.from("appointments")
                .select {
                    filter {
                        eq("shop_id", shopId)
                        eq("appointment_date", date.toString())
                    }
                }
                .decodeList<JsonObject>()

            val appointments = result.map { json ->
                Appointment(
                    id = json["id"]?.jsonPrimitive?.content ?: "",
                    customerId = json["customer_id"]?.jsonPrimitive?.content ?: "",
                    shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
                    slotId = json["slot_id"]?.jsonPrimitive?.content ?: "",
                    appointmentDate = LocalDate.parse(json["appointment_date"]?.jsonPrimitive?.content ?: ""),
                    timeSlot = LocalTime.parse(json["time_slot"]?.jsonPrimitive?.content ?: ""),
                    status = AppointmentStatus.fromString(json["status"]?.jsonPrimitive?.content ?: "pending"),
                    paymentMethod = json["payment_method"]?.jsonPrimitive?.content ?: "cash_on_arrival"
                )
            }

            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
