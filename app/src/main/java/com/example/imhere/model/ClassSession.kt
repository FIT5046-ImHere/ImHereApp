package com.example.imhere.model

import java.util.Date

data class ClassSession(
    val id: String? = null,
    val name: String = "",
    val location: String = "",
    val unitCode: String = "",
    val recurrence: String = "weekly",
    val startDateTime: Date = Date(),
    val endDateTime: Date = Date()
)