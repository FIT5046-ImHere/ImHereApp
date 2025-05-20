package com.example.imhere.model.service.impl

import com.example.imhere.model.ClassSession
import com.example.imhere.model.service.ClassSessionService
import com.example.imhere.model.service.EnrollmentService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EnrollmentServiceImpl @Inject constructor(
    firestore: FirebaseFirestore
) :     EnrollmentService {

    private val classCollection = firestore.collection("classTable")
    // this has both students and teachers, as well as the class codes, so just match teh subject code with class ID

    override suspend fun enrollStudentToClassSession(studentId: String, classSessionId: String) {
        
    }

    override suspend fun getEnrollments(studentId: String?, classSessionId: String?) {
        var query = classCollection
        // the enrollments for each class should be in the array, enrollment refers to the students
    }
}
