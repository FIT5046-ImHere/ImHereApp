package com.example.imhere.db

import UserProfile
import UserProfileDAO
import UserProfileDatabase
import android.app.Application
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

class UserProfileRepository (application: Application) {
    private var UserProfileDAO: UserProfileDAO =
        UserProfileDatabase.getDatabase(application).userProfileDAO()
    val allSubjects: Flow<List<UserProfile>> = UserProfileDAO.getAllSubjects()
    suspend fun insert(subject: UserProfile) {
        UserProfileDAO.insertSubject(subject)
    }
    suspend fun delete(subject: UserProfile) {
        UserProfileDAO.deleteSubject(subject)
    }
    suspend fun update(subject: UserProfile) {
        UserProfileDAO.updateSubject(subject)
    }
}