package com.example.imhere.model.service

import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.StudentAttendance
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface AttendanceService {
    suspend fun createAttendance(
        studentId: String,
        teacherId: String,
        classSessionId: String,
        dateTime: Date,
        status: AttendanceStatus
    ): Attendance
    suspend fun checkAttendancePassword(
        classSessionId: String,
        password: String
    ): Boolean
    suspend fun startTakingAttendances(
        classSessionId: String
    ): String
    suspend fun saveAttendances(classSessionId: String)

    suspend fun getCurrentAttendances(classSessionId: String): List<Attendance>

    suspend fun getAttendances(
        studentId: String?,
        teacherId: String?,
        classSessionId: String?,
        startDate: Date?,
        endDate: Date?
    ): List<Attendance>

    fun observeAttendances(
        studentId: String?,
        teacherId: String?,
        classSessionId: String?,
        startDate: Date?,
        endDate: Date?
    ): Flow<List<Attendance>>

    fun observeStudentAttendances(
        classSessionId: String
    ): Flow<List<StudentAttendance>>
}