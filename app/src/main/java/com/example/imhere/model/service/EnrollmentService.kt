package com.example.imhere.model.service

interface EnrollmentService {
    suspend fun enrollStudentToClassSession(studentId: String, classSessionId: String)
    suspend fun getEnrollments(studentId: String?, classSessionId: String?)
}