package com.koupa.barberbooking.data.mapper

import com.koupa.barberbooking.domain.model.Appointment
import com.koupa.barberbooking.domain.model.AppointmentStatus
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * Mapper for Appointment entity.
 * Converts between domain model and Supabase JSON representation.
 */
object AppointmentMapper {

    fun fromJson(json: JsonObject): Appointment {
        // Extract service name from barbershops.services array (first service or joined)
        val serviceName = json["service_name"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
            ?: run {
                val barbershop = json["barbershops"]
                if (barbershop is JsonObject) {
                    val services = barbershop["services"]
                    if (services is JsonArray) {
                        services.map { it.jsonPrimitive?.content?.trim() ?: "" }
                            .firstOrNull { it.isNotBlank() }
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            ?: ""

        return Appointment(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            customerId = json["customer_id"]?.jsonPrimitive?.content ?: "",
            shopId = json["shop_id"]?.jsonPrimitive?.content ?: "",
            slotId = json["slot_id"]?.jsonPrimitive?.content ?: "",
            appointmentDate = LocalDate.parse(json["appointment_date"]?.jsonPrimitive?.content ?: ""),
            timeSlot = LocalTime.parse(json["time_slot"]?.jsonPrimitive?.content ?: ""),
            status = AppointmentStatus.fromString(json["status"]?.jsonPrimitive?.content ?: "pending"),
            paymentMethod = json["payment_method"]?.jsonPrimitive?.content ?: "cash_on_arrival",
            customerName = json["customer_name"]?.jsonPrimitive?.content ?: "",
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
