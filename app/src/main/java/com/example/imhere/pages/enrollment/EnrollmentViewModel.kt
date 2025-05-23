package com.example.imhere.pages.enrollment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.Enrollment
import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.EnrollmentService
import com.example.imhere.model.service.StudentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val enrollmentService: EnrollmentService,
    private val studentService: StudentService
) : ViewModel() {

    var students = MutableStateFlow<List<UserProfile>>(emptyList())
    var selectedIds = MutableStateFlow<Set<String>>(emptySet())
    var isLoading = MutableStateFlow(false)
    var success = MutableStateFlow<List<Enrollment>?>(null)

//    init {
//        fetchStudents()
//    }

    fun load(classSessionId: String) {
        viewModelScope.launch {
            isLoading.value = true

            val allStudents = studentService.getStudents()
            students.value = allStudents

            val enrollments = enrollmentService.getEnrollments(null, classSessionId)

            val enrolledStudentIds = enrollments.map { it.studentId }.toSet()
            selectedIds.value = enrolledStudentIds

            isLoading.value = false
        }
    }

//    private fun fetchStudents() {
//        viewModelScope.launch {
//            students.value = studentService.getStudents()
//        }
//    }

    fun toggleSelection(studentId: String) {
        val current = selectedIds.value.toMutableSet()
        if (current.contains(studentId)) current.remove(studentId)
        else current.add(studentId)
        selectedIds.value = current
    }

    fun enroll(classSessionId: String) {
        if (selectedIds.value.isEmpty()) return

        viewModelScope.launch {
            isLoading.value = true
            success.value = enrollmentService.enrollStudentsToClassSession(
                studentIds = selectedIds.value.toList(),
                classSessionId = classSessionId
            )
            isLoading.value = false
        }
    }
}

