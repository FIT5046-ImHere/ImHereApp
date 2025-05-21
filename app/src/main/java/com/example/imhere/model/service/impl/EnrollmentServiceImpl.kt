package com.example.imhere.model.service.impl

import com.example.imhere.model.Enrollment
import com.example.imhere.model.service.EnrollmentService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class EnrollmentServiceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : EnrollmentService {
    private val collection = firestore.collection("enrollments")

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