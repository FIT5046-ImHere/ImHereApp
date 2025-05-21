package com.example.imhere.model.service.impl

import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.StudentService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentService {

    private val usersCollection = firestore.collection("users")

    override suspend fun getStudents(): List<UserProfile> {
        val snapshot = usersCollection
            .whereEqualTo("type", "student")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
    }

    override suspend fun getStudentsByClassSessionId(classSessionId: String): List<UserProfile> {
        val enrollmentSnapshot = firestore.collection("enrollments")
            .whereEqualTo("classSessionId", classSessionId)
            .get()
            .await()

        val studentIds = enrollmentSnapshot.documents.mapNotNull { it.getString("studentId") }

        if (studentIds.isEmpty()) return emptyList()

        val studentProfiles = mutableListOf<UserProfile>()

        studentIds.chunked(10).forEach { batch ->
            val studentSnapshot = usersCollection
                .whereIn("uid", batch)
                .get()
                .await()

            studentProfiles += studentSnapshot.documents.mapNotNull {
                it.toObject(UserProfile::class.java)
            }
        }

        return studentProfiles
    }
}
