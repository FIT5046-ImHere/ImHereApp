package com.example.imhere.di

import com.example.imhere.model.service.*
import com.example.imhere.model.service.impl.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
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
    fun provideFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }

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

    @Provides
    @Singleton
    fun provideAttendanceService(
        firestore: FirebaseFirestore
    ): AttendanceService = AttendanceServiceImpl(firestore)

    @Provides
    @Singleton
    fun provideEnrollmentService(
        firestore: FirebaseFirestore
    ): EnrollmentService = EnrollmentServiceImpl(firestore)

    @Provides
    @Singleton
    fun provideStudentService(
        firestore: FirebaseFirestore
    ): StudentService = StudentServiceImpl(firestore)
}
