package com.example.imhere.model.service

import com.example.imhere.model.Enrollment

interface EnrollmentService {
    suspend fun enrollStudentToClassSession(studentId: String, classSessionId: String): Enrollment
    suspend fun enrollStudentsToClassSession(studentIds: List<String>, classSessionId: String): List<Enrollment>?
    suspend fun getEnrollments(studentId: String?, classSessionId: String?): List<Enrollment>
}