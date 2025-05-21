package com.example.imhere.mock_data

import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.*

object AttendanceMockData {
    private val statuses = AttendanceStatus.entries.toTypedArray()
    private val sessionIds = (1..10).map { it.toString() }
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val attendanceList: List<Attendance> = List(30) { index ->
        val dayOffset = index % 15
        val baseDate = dateFormatter.parse("2025-05-01")!!
        val date = Date(baseDate.time + dayOffset * 86_400_000L)

        Attendance(
            studentId = UUID.randomUUID().toString(),
            classSessionId = sessionIds.random(),
            dateTime = date,
            status = statuses.random()
        )
    }
}
