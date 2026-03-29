package com.koupa.barberbooking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "barber_shops")
data class BarberShopEntity(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val openingTime: LocalTime,
    val closingTime: LocalTime,
    val rating: Double,
    val services: List<String>,
    val profileImage: String,
    val isShopOpen: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)