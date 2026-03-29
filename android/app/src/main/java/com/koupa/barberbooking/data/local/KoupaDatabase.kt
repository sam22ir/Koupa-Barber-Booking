package com.koupa.barberbooking.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.koupa.barberbooking.data.local.converter.DateConverter
import com.koupa.barberbooking.data.local.converter.ListConverter
import com.koupa.barberbooking.data.local.dao.AppointmentDao
import com.koupa.barberbooking.data.local.dao.BarberShopDao
import com.koupa.barberbooking.data.local.dao.ReviewDao
import com.koupa.barberbooking.data.local.dao.ServiceDao
import com.koupa.barberbooking.data.local.dao.UserDao
import com.koupa.barberbooking.data.local.entity.AppointmentEntity
import com.koupa.barberbooking.data.local.entity.BarberShopEntity
import com.koupa.barberbooking.data.local.entity.ReviewEntity
import com.koupa.barberbooking.data.local.entity.ServiceEntity
import com.koupa.barberbooking.data.local.entity.UserEntity

@Database(
    entities = [
        BarberShopEntity::class,
        UserEntity::class,
        AppointmentEntity::class,
        ServiceEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class KoupaDatabase : RoomDatabase() {

    abstract fun barberShopDao(): BarberShopDao
    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun serviceDao(): ServiceDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        const val DATABASE_NAME = "koupa-database"
    }
}