package com.example.imhere.model

import java.util.Date

enum class AttendanceStatus(val value: String) {
    PRESENT("present"),
    LATE("late"),
    ABSENT("absent"),
    present("present"),
    late("late"),
    absent("absent")
}

data class Attendance(
    val studentId: String = "",
    val teacherId: String = "",
    val classSessionId: String = "",
    val dateTime: Date = Date(),
    val status: AttendanceStatus = AttendanceStatus.PRESENT
)

