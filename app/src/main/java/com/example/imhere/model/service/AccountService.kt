package com.example.imhere.model.service

import com.example.imhere.model.AuthUser
import com.example.imhere.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean

    val currentAuthUser: Flow<AuthUser>

    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun sendRecoveryEmail(email: String)
    suspend fun createAnonymousAccount()
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun createUserProfile(uid: String, profile: UserProfile)
    suspend fun updateUserProfile(uid: String, profile: UserProfile)
    suspend fun fetchUserProfile(uid: String): UserProfile?
}