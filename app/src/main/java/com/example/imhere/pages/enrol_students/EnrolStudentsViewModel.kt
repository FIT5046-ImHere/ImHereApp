package com.example.imhere.pages.enrol_students

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.ClassSessionService
import com.example.imhere.model.service.EnrollmentService
import com.example.imhere.model.service.StudentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnrolStudentsViewModel @Inject constructor(
    private val classSessionService: ClassSessionService,
    private val studentService: StudentService,
    private val enrollmentService: EnrollmentService,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val classId    = checkNotNull(savedStateHandle["classId"])
    val className         = savedStateHandle["className"] ?: "Select Students"

    private val _students      = MutableStateFlow<List<UserProfile>>(emptyList())
    val students: StateFlow<List<UserProfile>> = _students.asStateFlow()

    private val _selectedUids  = MutableStateFlow<Set<String>>(emptySet())
    val selectedUids: StateFlow<Set<String>> = _selectedUids.asStateFlow()

    private val _isSubmitting  = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _students.value = studentService.getStudents()
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun toggle(uid: String) {
        _selectedUids.value = _selectedUids.value.let {
            if (uid in it) it - uid else it + uid
        }
    }

    fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                enrollmentService.enrollStudentToClassSession(
                    classSessionId = classId.toString(),
                    studentId = "",
//                    studentUids = emptyList()
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to enrol")
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
