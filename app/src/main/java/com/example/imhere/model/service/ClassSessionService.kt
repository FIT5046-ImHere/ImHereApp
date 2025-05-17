package com.example.imhere.model.service

import com.example.imhere.model.ClassSession

interface ClassSessionService {
    suspend fun getAllClassesByStudentId(studentId: String): List<ClassSession>
    suspend fun getClassById(classSessionId: String): ClassSession?
    suspend fun createClassSession(classSession: ClassSession)
    suspend fun deleteClassSession(classSessionId: String)
}