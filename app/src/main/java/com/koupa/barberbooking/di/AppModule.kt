package com.koupa.barberbooking.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.koupa.barberbooking.data.datasource.remote.FirebaseAuthDataSource
import com.koupa.barberbooking.data.datasource.remote.FirebaseMessagingDataSource
import com.koupa.barberbooking.data.repository.AuthRepositoryImpl
import com.koupa.barberbooking.data.repository.BarberShopRepositoryImpl
import com.koupa.barberbooking.domain.repository.AuthRepository
import com.koupa.barberbooking.domain.repository.BarberShopRepository
import javax.inject.Singleton

/**
 * Hilt dependency injection module.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        auth: FirebaseAuth
    ): FirebaseAuthDataSource = FirebaseAuthDataSource(auth)

    @Provides
    @Singleton
    fun provideFirebaseMessagingDataSource(
        messaging: FirebaseMessaging
    ): FirebaseMessagingDataSource = FirebaseMessagingDataSource(messaging)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthRepository = AuthRepositoryImpl(firebaseAuthDataSource)

    @Provides
    @Singleton
    fun provideBarberShopRepository(): BarberShopRepository = BarberShopRepositoryImpl()
}
