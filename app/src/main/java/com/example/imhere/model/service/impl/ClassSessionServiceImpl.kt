package com.example.imhere.model.service.impl

import com.example.imhere.model.ClassSession
import com.example.imhere.model.service.ClassSessionService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassSessionServiceImpl @Inject constructor(
    firestore: FirebaseFirestore
) : ClassSessionService {

    private val collection = firestore.collection("classSessions")

    override suspend fun getAllClassSessions(
        studentId: String?,
        teacherId: String?
    ): List<ClassSession> {
        var query = collection

        if (studentId != null) {
            query = query.whereArrayContains("studentIds", studentId) as CollectionReference
        }

        if (teacherId != null) {
            query = query.whereEqualTo("teacherId", teacherId) as CollectionReference
        }

        val snapshot = query.get().await()
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

    override suspend fun createClassSession(classSession: ClassSession): ClassSession {
        val docRef = if (classSession.id != null) {
            collection.document(classSession.id)
        } else {
            collection.document()
        }

        val finalClassSession = classSession.copy(id = docRef.id)

        docRef.set(finalClassSession).await()

        return finalClassSession
    }

    override suspend fun deleteClassSession(classSessionId: String) {
        collection.document(classSessionId).delete().await()
    }
}
