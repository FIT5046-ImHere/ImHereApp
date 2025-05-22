package com.example.imhere.model.service.impl

import com.example.imhere.model.Enrollment
import com.example.imhere.model.service.EnrollmentService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class EnrollmentServiceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : EnrollmentService {
    private val collection = firestore.collection("enrollments")
    private val classSessionCollection = firestore.collection("classSessions")

    private val batch = firestore.batch()

    override suspend fun enrollStudentToClassSession(studentId: String, classSessionId: String): Enrollment {
        val enrollment = Enrollment(
            studentId = studentId,
            classSessionId = classSessionId,
            timestamp = Date()
        )

        val docId = "${studentId}_$classSessionId"
        collection.document(docId).set(enrollment).await()

        return enrollment
    }

    override suspend fun enrollStudentsToClassSession(
        studentIds: List<String>,
        classSessionId: String
    ): List<Enrollment>? {
        if (studentIds.isEmpty()) return emptyList()

        return try {
            val now = Date()

            val enrollments = studentIds.map { studentId ->
                val enrollment = Enrollment(
                    studentId = studentId,
                    classSessionId = classSessionId,
                    timestamp = now
                )
                val docRef = collection.document()
                batch.set(docRef, enrollment)
                enrollment
            }

            // Also update the class session's document to include these student IDs
            val classSessionRef = classSessionCollection.document(classSessionId)
            batch.update(classSessionRef, "studentIds", FieldValue.arrayUnion(*studentIds.toTypedArray()))

            batch.commit().await()
            enrollments
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    override suspend fun getEnrollments(studentId: String?, classSessionId: String?): List<Enrollment> {
        var query = collection.limit(100)

        if (studentId != null) {
            query = query.whereEqualTo("studentId", studentId)
        }

        if (classSessionId != null) {
            query = query.whereEqualTo("classSessionId", classSessionId)
        }

        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Enrollment::class.java) }
    }

}