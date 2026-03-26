package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.Appointment
import com.koupa.barberbooking.domain.model.AppointmentStatus
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.LocalTime

/**
 * Mapper for Appointment entity.
 * Converts between domain model and Supabase JSON representation.
 */
object AppointmentMapper {

    fun fromJson(json: JsonObject): Appointment {
        // Extract service name from barbershops.services array (first service or joined)
        val serviceName = json["barbershops"]?.jsonObject?.get("services")?.jsonArray
            ?.map { it.jsonPrimitive?.content ?: "" }
            ?.filter { it.isNotBlank() }
            ?.firstOrNull() ?: json["service_name"]?.jsonPrimitive?.content

        return Appointment(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            customerId = json["customer_id"]?.jsonPrimitive?.content ?: "",
            shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
            slotId = json["slot_id"]?.jsonPrimitive?.content ?: "",
            appointmentDate = LocalDate.parse(json["appointment_date"]?.jsonPrimitive?.content ?: ""),
            timeSlot = LocalTime.parse(json["time_slot"]?.jsonPrimitive?.content ?: ""),
            status = AppointmentStatus.fromString(json["status"]?.jsonPrimitive?.content ?: "pending"),
            paymentMethod = json["payment_method"]?.jsonPrimitive?.content ?: "cash_on_arrival",
            customerName = json["customer_name"]?.jsonPrimitive?.content,
            serviceName = serviceName
        )
    }

    fun toJson(appointment: Appointment): JsonObject {
        return buildJsonObject {
            put("id", appointment.id)
            put("customer_id", appointment.customerId)
            put("shop_id", appointment.shopId)
            put("slot_id", appointment.slotId)
            put("appointment_date", appointment.appointmentDate.toString())
            put("time_slot", appointment.timeSlot.toString())
            put("status", appointment.status.toApiString())
            put("payment_method", appointment.paymentMethod)
        }
    }
}
