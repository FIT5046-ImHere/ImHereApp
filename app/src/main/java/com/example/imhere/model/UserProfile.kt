package com.example.imhere.model

import java.util.Date

enum class UserProfileType(val value: String) {
    STUDENT("student"),
    TEACHER("teacher");

    companion object {
        fun fromValue(value: String): UserProfileType {
            return entries.firstOrNull { it.value == value }
                ?: STUDENT
        }
    }
}

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val type: UserProfileType = UserProfileType.STUDENT,
    val birthDate: Date = Date()
)