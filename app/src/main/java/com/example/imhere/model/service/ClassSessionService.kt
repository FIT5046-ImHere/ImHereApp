package com.example.imhere.model.service

import com.example.imhere.model.ClassSession

interface ClassSessionService {
    suspend fun getAllClassSessions(
        studentId: String? = null,
        teacherId: String? = null
    ): List<ClassSession>
    suspend fun getClassById(classSessionId: String): ClassSession?
    suspend fun createClassSession(classSession: ClassSession): ClassSession
    suspend fun deleteClassSession(classSessionId: String)
}