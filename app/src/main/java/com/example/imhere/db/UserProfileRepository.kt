package com.example.imhere.db

import UserProfileEntity
import UserProfileDAO
import UserProfileDatabase
import android.app.Application
import android.util.Log
import kotlinx.coroutines.flow.Flow

class UserProfileRepository (application: Application) {
    private var UserProfileDAO: UserProfileDAO =
        UserProfileDatabase.getDatabase(application).userProfileDAO()
    val allSubjects: Flow<List<UserProfileEntity>> = UserProfileDAO.getAllSubjects()
    suspend fun insert(subject: UserProfileEntity) {
        UserProfileDAO.insertSubject(subject)
    }
    suspend fun delete(subject: UserProfileEntity) {
        UserProfileDAO.deleteSubject(subject)
    }
    suspend fun update(subject: UserProfileEntity) {
        UserProfileDAO.updateSubject(subject)
    }
    suspend fun upsert(subject: UserProfileEntity) {
        Log.d("UPSERTTTTTT", "Upsert")
        UserProfileDAO.upsertSubject(subject)
    }
}