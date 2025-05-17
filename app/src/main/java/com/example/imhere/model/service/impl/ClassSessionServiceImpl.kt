package com.example.imhere.model.service.impl

import com.example.imhere.model.ClassSession
import com.example.imhere.model.service.ClassSessionService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassSessionServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ClassSessionService {

    private val collection = firestore.collection("classSessions")

    override suspend fun getAllClassesByStudentId(studentId: String): List<ClassSession> {
        val snapshot = collection
            .whereArrayContains("studentIds", studentId) // assumes studentIds: List<String>
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(ClassSession::class.java) }
    }

    override suspend fun getClassById(classSessionId: String): ClassSession? {
        val snapshot = collection.document(classSessionId).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(ClassSession::class.java)
        } else {
            null
        }
    }

    override suspend fun createClassSession(classSession: ClassSession) {
        // Use the ID if it exists, or let Firestore generate one
        val docRef = if (classSession.id != null) {
            collection.document(classSession.id)
        } else {
            collection.document()
        }
        docRef.set(classSession).await()
    }

    override suspend fun deleteClassSession(classSessionId: String) {
        collection.document(classSessionId).delete().await()
    }
}
