package com.koupa.barberbooking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val profileImage: String,
    val isBarber: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)