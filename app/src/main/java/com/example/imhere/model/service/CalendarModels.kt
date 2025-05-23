package com.example.imhere.model.service

import com.example.imhere.model.ClassSessionRecurrence
import com.google.gson.annotations.SerializedName

/** The core event send/receive via the Calendar API */
data class CalendarEvent(
    val summary: String,
    val description: String? = null,
    val start: EventDateTime,
    val end: EventDateTime,
    val recurrence: List<String>? = null,
    val attendees: List<EventAttendee>? = null,
    val reminders: EventReminders? = null
)

data class EventDateTime(
    @SerializedName("dateTime") val dateTime: String,            // ISO 8601
    val timeZone: String = "Australia/Melbourne"
)

data class EventAttendee(
    val email: String
)

data class EventReminder(
    val method: String,   // "email" or "popup"
    val minutes: Int
)

data class EventReminders(
    @SerializedName("useDefault") val useDefault: Boolean = false,
    @SerializedName("overrides") val overrides: List<EventReminder>
)
