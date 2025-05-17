package com.example.imhere.di

import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.ClassSessionService
import com.example.imhere.model.service.impl.AccountServiceImpl
import com.example.imhere.model.service.impl.ClassSessionServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAccountService(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AccountService = AccountServiceImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideClassSessionService(
        firestore: FirebaseFirestore
    ): ClassSessionService = ClassSessionServiceImpl(firestore)
}