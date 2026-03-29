package com.koupa.barberbooking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey
    val id: String,
    val customerId: String,
    val customerName: String,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val status: String,
    val shopId: String,
    val serviceId: String,
    val userId: String,
    val service: String,
    val createdAt: Long = System.currentTimeMillis()
)