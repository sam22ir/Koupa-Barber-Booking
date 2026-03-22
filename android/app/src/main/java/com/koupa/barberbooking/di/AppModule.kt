package com.koupa.barberbooking.di

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.koupa.barberbooking.data.datasource.remote.FirebaseMessagingDataSource
import com.koupa.barberbooking.data.repository.AuthRepositoryImpl
import com.koupa.barberbooking.data.repository.BarberShopRepositoryImpl
import com.koupa.barberbooking.data.repository.NotificationRepositoryImpl
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import com.koupa.barberbooking.domain.repository.NotificationRepository
import javax.inject.Singleton

/**
 * Hilt dependency injection module.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMessagingDataSource(
        messaging: FirebaseMessaging
    ): FirebaseMessagingDataSource = FirebaseMessagingDataSource(messaging)

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    @Singleton
    fun provideBarberShopRepository(): BarberShopRepository = BarberShopRepositoryImpl()

    @Provides
    @Singleton
    fun provideNotificationRepository(
        fcmDataSource: FirebaseMessagingDataSource
    ): NotificationRepository = NotificationRepositoryImpl(fcmDataSource)
}
