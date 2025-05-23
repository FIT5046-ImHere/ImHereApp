package com.example.imhere.db

import androidx.room.TypeConverter
import com.example.imhere.model.UserProfileType
import java.util.*

class UserProfileTypeConverter {
    @TypeConverter
    fun fromEnum(value: UserProfileType): String = value.value

    @TypeConverter
    fun toEnum(value: String): UserProfileType = UserProfileType.fromValue(value)
}

class DateConverter {
    @TypeConverter
    fun fromDate(value: Date): Long = value.time

    @TypeConverter
    fun toDate(value: Long): Date = Date(value)
}