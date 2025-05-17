package com.example.imhere.di

import com.example.imhere.model.service.AccountService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AccountServiceEntryPoint {
    fun accountService(): AccountService
}