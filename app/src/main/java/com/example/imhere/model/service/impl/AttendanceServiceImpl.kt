package com.example.imhere.model.service.impl

import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.StudentAttendance
import com.example.imhere.model.service.AttendanceService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AttendanceServiceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : AttendanceService {

    private val collection = firestore.collection("attendances")
    private val userCollection = firestore.collection("users")

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

    override fun observeAttendances(
        studentId: String?,
        teacherId: String?,
        classSessionId: String?,
        startDate: Date?,
        endDate: Date?
    ): Flow<List<Attendance>> = callbackFlow {
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

        val listenerRegistration: ListenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val attendances = snapshot?.documents?.mapNotNull { it.toObject(Attendance::class.java) }
            trySend(attendances ?: emptyList())
        }

        awaitClose { listenerRegistration.remove() }
    }

    override fun observeStudentAttendances(
        classSessionId: String
    ): Flow<List<StudentAttendance>> = callbackFlow {
        val attendanceQuery = collection
            .whereEqualTo("classSessionId", classSessionId)

        val listenerRegistration = attendanceQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val attendanceDocs = snapshot?.documents ?: emptyList()

            launch {
                val studentAttendances = attendanceDocs.mapNotNull { doc ->
                    val attendance = doc.toObject(Attendance::class.java)
                    val attendanceId = doc.id

                    if (attendance != null) {
                        val studentSnapshot = userCollection
                            .document(attendance.studentId)
                            .get()
                            .await()
                        val studentName = studentSnapshot.getString("name") ?: attendance.studentId

                        StudentAttendance(
                            studentName = studentName,
                            attendanceId = attendanceId,
                            status = attendance.status
                        )
                    } else null
                }

                trySend(studentAttendances)
            }
        }

        awaitClose { listenerRegistration.remove() }
    }

}

