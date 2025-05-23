package com.example.imhere.mock_data

import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.*

object SelfAttendanceMockData {
    val studentId = "DyZCoUTXeJUDmnx3yDbFzPBeh3l2"
    private val sessionIds = (1..10).map { it.toString() }
    private val statuses = AttendanceStatus.entries.toTypedArray()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val attendanceList: List<Attendance> = List(30) { index ->
        // Cycle through May 1st to May 15th, 2025
        val dayOffset = index % 15 // 0 to 14
        val baseDate = dateFormatter.parse("2025-05-01")!!
        val date = Date(baseDate.time + dayOffset * 24 * 60 * 60 * 1000L)

        Attendance(
            studentId = studentId,
            classSessionId = sessionIds.random(),
            dateTime = date,
            status = statuses.random()
        )
    }
}
