package com.example.imhere.model

data class StudentAttendance (
    val studentName: String = "",
    val attendanceId: String = "",
    val status: AttendanceStatus = AttendanceStatus.PRESENT
)
