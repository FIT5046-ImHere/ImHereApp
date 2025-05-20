package com.example.imhere.model.service.impl

import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.service.AttendanceService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AttendanceServiceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : AttendanceService {

    private val collection = firestore.collection("attendances")

    override suspend fun startTakingAttendances(classSessionId: String): String {
        val password = UUID.randomUUID().toString()
        val passwordExpireAt = Date(System.currentTimeMillis() + 15 * 60 * 1000) // expires in 15 mins

        val classSessionRef = collection.document(classSessionId)

        classSessionRef.update(
            mapOf(
                "attendancePassword" to password,
                "passwordExpireAt" to passwordExpireAt
            )
        ).await()

        return password
    }

    override suspend fun createAttendance(
        studentId: String,
        teacherId: String,
        classSessionId: String,
        dateTime: Date,
        status: AttendanceStatus
    ): Attendance {
        val attendance = Attendance(
            studentId = studentId,
            teacherId = teacherId,
            classSessionId = classSessionId,
            dateTime = dateTime,
            status = status
        )

        collection.add(attendance).await()
        return attendance
    }

    override suspend fun getAttendances(
        studentId: String?,
        teacherId: String?,
        classSessionId: String?,
        startDate: Date?,
        endDate: Date?
    ): List<Attendance> {
        var query = collection as com.google.firebase.firestore.Query

        if (studentId != null) {
            query = query.whereEqualTo("studentId", studentId)
        }
        if (teacherId != null) {
            query = query.whereEqualTo("teacherId", teacherId)
        }
        if (classSessionId != null) {
            query = query.whereEqualTo("classSessionId", classSessionId)
        }
        if (startDate != null) {
            query = query.whereGreaterThanOrEqualTo("dateTime", startDate)
        }
        if (endDate != null) {
            query = query.whereLessThanOrEqualTo("dateTime", endDate)
        }

        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Attendance::class.java) }
    }

    override suspend fun checkAttendancePassword(
        classSessionId: String,
        password: String
    ): Boolean {
        val classSessionSnap = collection
            .document(classSessionId)
            .get()
            .await()

        if (!classSessionSnap.exists()) return false

        val storedPassword = classSessionSnap.getString("attendancePassword")
        val expireAt = classSessionSnap.getDate("passwordExpireAt")

        val now = Date()

        return storedPassword == password && expireAt != null && now.before(expireAt)
    }
}

