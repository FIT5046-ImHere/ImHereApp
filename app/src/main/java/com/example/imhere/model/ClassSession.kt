package com.example.imhere.model

import java.util.Date

enum class ClassSessionRecurrence(val value: String) {
    ONCE("once"),
    WEEKLY("weekly"),
    BIWEEKLY("biweekly"),
    MONTHLY("monthly"),
}

data class ClassSession(
    val id: String? = null,
    val name: String = "",
    val location: String = "",
    val unitCode: String = "",
    val recurrence: ClassSessionRecurrence = ClassSessionRecurrence.ONCE,
    val startDateTime: Date = Date(),
    val endDateTime: Date = Date()
)