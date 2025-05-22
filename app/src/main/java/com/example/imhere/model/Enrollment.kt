package com.example.imhere.model

import java.util.Date

data class Enrollment(
    val studentId: String = "",
    val classSessionId: String = "",
    val timestamp: Date = Date()
)