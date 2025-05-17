package com.example.imhere.model

import java.util.Date

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val type: String = "student", // default value, could be "teacher"
    val birthDate: Date
)