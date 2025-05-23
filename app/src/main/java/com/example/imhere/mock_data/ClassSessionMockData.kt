package com.example.imhere.mock_data

import com.example.imhere.model.ClassSession
import com.example.imhere.model.ClassSessionRecurrence
import java.text.SimpleDateFormat
import java.util.*

object ClassSessionMockData {

    private val recurrenceOptions = ClassSessionRecurrence.entries.toTypedArray()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    val classSessions: List<ClassSession> = List(10) { index ->
        val baseDate = Calendar.getInstance().apply {
            set(2025, Calendar.MAY, 1 + index) // May 1st–10th
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }

        val startDate = baseDate.time
        val endDate = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.HOUR, 2) // 2-hour session
        }.time

        ClassSession(
            teacherId = UUID.randomUUID().toString(),
            id = index.toString(),
            name = "Session ${index + 1}",
            location = "Room ${100 + index}",
            unitCode = "FIT50${index + 1}",
            recurrence = recurrenceOptions.random(),
            startDateTime = startDate,
            endDateTime = endDate
        )
    }
}
