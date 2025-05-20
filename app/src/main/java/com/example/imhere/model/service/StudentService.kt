package com.example.imhere.model.service

import com.example.imhere.model.UserProfile

interface StudentService {
    suspend fun getStudents(): List<UserProfile>
    suspend fun getStudentsByClassSessionId(classSessionId: String): List<UserProfile>
}