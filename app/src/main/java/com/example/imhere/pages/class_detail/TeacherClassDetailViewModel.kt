package com.example.imhere.pages.class_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.ClassSession
import com.example.imhere.model.StudentAttendance
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.AttendanceService
import com.example.imhere.model.service.ClassSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherClassDetailViewModel @Inject constructor(
    private val attendanceService: AttendanceService,
    private val classSessionService: ClassSessionService
) : ViewModel() {
    val isSaving = MutableStateFlow(false)
    val classSession = MutableStateFlow<ClassSession?>(null)
    val isLoading = MutableStateFlow(true)

    fun loadClassSession(classSessionId: String) {
        viewModelScope.launch {
            isLoading.value = true
            val session = classSessionService.getClassById(classSessionId)
            classSession.value = session
            isLoading.value = false
        }
    }

    fun getStudentAttendances(classSessionId: String): Flow<List<StudentAttendance>> {
        return attendanceService.observeStudentAttendances(classSessionId)
    }

    fun saveAttendances(classSessionId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSaving.value = true

            try {
                attendanceService.saveAttendances(classSessionId)
                onSuccess()
            } finally {
                isSaving.value = false
            }
        }
    }
}